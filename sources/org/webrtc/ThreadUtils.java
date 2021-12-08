package org.webrtc;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    public interface BlockingOperation {
        void run() throws InterruptedException;
    }

    public static class ThreadChecker {
        private Thread thread = Thread.currentThread();

        public void checkIsOnValidThread() {
            if (this.thread == null) {
                this.thread = Thread.currentThread();
            }
            if (Thread.currentThread() != this.thread) {
                throw new IllegalStateException("Wrong thread");
            }
        }

        public void detachThread() {
            this.thread = null;
        }
    }

    public static void checkIsOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("Not on main thread!");
        }
    }

    public static void executeUninterruptibly(BlockingOperation operation) {
        boolean wasInterrupted = false;
        while (true) {
            try {
                operation.run();
                break;
            } catch (InterruptedException e) {
                wasInterrupted = true;
            }
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean joinUninterruptibly(Thread thread, long timeoutMs) {
        long startTimeMs = SystemClock.elapsedRealtime();
        long timeRemainingMs = timeoutMs;
        boolean wasInterrupted = false;
        while (timeRemainingMs > 0) {
            try {
                thread.join(timeRemainingMs);
                break;
            } catch (InterruptedException e) {
                wasInterrupted = true;
                timeRemainingMs = timeoutMs - (SystemClock.elapsedRealtime() - startTimeMs);
            }
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
        return !thread.isAlive();
    }

    public static void joinUninterruptibly(final Thread thread) {
        executeUninterruptibly(new BlockingOperation() {
            public void run() throws InterruptedException {
                thread.join();
            }
        });
    }

    public static void awaitUninterruptibly(final CountDownLatch latch) {
        executeUninterruptibly(new BlockingOperation() {
            public void run() throws InterruptedException {
                latch.await();
            }
        });
    }

    public static boolean awaitUninterruptibly(CountDownLatch barrier, long timeoutMs) {
        long startTimeMs = SystemClock.elapsedRealtime();
        long timeRemainingMs = timeoutMs;
        boolean wasInterrupted = false;
        boolean result = false;
        while (true) {
            try {
                result = barrier.await(timeRemainingMs, TimeUnit.MILLISECONDS);
                break;
            } catch (InterruptedException e) {
                wasInterrupted = true;
                timeRemainingMs = timeoutMs - (SystemClock.elapsedRealtime() - startTimeMs);
                if (timeRemainingMs <= 0) {
                    break;
                }
            }
        }
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    public static <V> V invokeAtFrontUninterruptibly(Handler handler, final Callable<V> callable) {
        if (handler.getLooper().getThread() == Thread.currentThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            final AnonymousClass1Result result = new Object() {
                public V value;
            };
            final AnonymousClass1CaughtException caughtException = new Object() {
                Exception e;
            };
            final CountDownLatch barrier = new CountDownLatch(1);
            handler.post(new Runnable() {
                public void run() {
                    try {
                        AnonymousClass1Result.this.value = callable.call();
                    } catch (Exception e) {
                        caughtException.e = e;
                    }
                    barrier.countDown();
                }
            });
            awaitUninterruptibly(barrier);
            if (caughtException.e == null) {
                return result.value;
            }
            RuntimeException runtimeException = new RuntimeException(caughtException.e);
            runtimeException.setStackTrace(concatStackTraces(caughtException.e.getStackTrace(), runtimeException.getStackTrace()));
            throw runtimeException;
        }
    }

    public static void invokeAtFrontUninterruptibly(Handler handler, final Runnable runner) {
        invokeAtFrontUninterruptibly(handler, new Callable<Void>() {
            public Void call() {
                runner.run();
                return null;
            }
        });
    }

    static StackTraceElement[] concatStackTraces(StackTraceElement[] inner, StackTraceElement[] outer) {
        StackTraceElement[] combined = new StackTraceElement[(inner.length + outer.length)];
        System.arraycopy(inner, 0, combined, 0, inner.length);
        System.arraycopy(outer, 0, combined, inner.length, outer.length);
        return combined;
    }
}

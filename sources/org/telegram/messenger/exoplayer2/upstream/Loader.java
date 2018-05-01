package org.telegram.messenger.exoplayer2.upstream;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Loader implements LoaderErrorThrower {
    public static final int DONT_RETRY = 2;
    public static final int DONT_RETRY_FATAL = 3;
    public static final int RETRY = 0;
    public static final int RETRY_RESET_ERROR_COUNT = 1;
    private LoadTask<? extends Loadable> currentTask;
    private final ExecutorService downloadExecutorService;
    private IOException fatalError;

    public interface Callback<T extends Loadable> {
        void onLoadCanceled(T t, long j, long j2, boolean z);

        void onLoadCompleted(T t, long j, long j2);

        int onLoadError(T t, long j, long j2, IOException iOException);
    }

    @SuppressLint({"HandlerLeak"})
    private final class LoadTask<T extends Loadable> extends Handler implements Runnable {
        private static final int MSG_CANCEL = 1;
        private static final int MSG_END_OF_SOURCE = 2;
        private static final int MSG_FATAL_ERROR = 4;
        private static final int MSG_IO_EXCEPTION = 3;
        private static final int MSG_START = 0;
        private static final String TAG = "LoadTask";
        private final Callback<T> callback;
        private IOException currentError;
        public final int defaultMinRetryCount;
        private int errorCount;
        private volatile Thread executorThread;
        private final T loadable;
        private volatile boolean released;
        private final long startTimeMs;

        public LoadTask(Looper looper, T t, Callback<T> callback, int i, long j) {
            super(looper);
            this.loadable = t;
            this.callback = callback;
            this.defaultMinRetryCount = i;
            this.startTimeMs = j;
        }

        public void maybeThrowError(int i) throws IOException {
            if (this.currentError != null && this.errorCount > i) {
                throw this.currentError;
            }
        }

        public void start(long j) {
            Assertions.checkState(Loader.this.currentTask == null);
            Loader.this.currentTask = this;
            if (j > 0) {
                sendEmptyMessageDelayed(0, j);
            } else {
                execute();
            }
        }

        public void cancel(boolean z) {
            this.released = z;
            this.currentError = null;
            if (hasMessages(0)) {
                removeMessages(0);
                if (!z) {
                    sendEmptyMessage(1);
                }
            } else {
                this.loadable.cancelLoad();
                if (this.executorThread != null) {
                    this.executorThread.interrupt();
                }
            }
            if (z) {
                finish();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                this.callback.onLoadCanceled(this.loadable, elapsedRealtime, elapsedRealtime - this.startTimeMs, true);
            }
        }

        public void run() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r4 = this;
            r0 = 2;
            r1 = 3;
            r2 = java.lang.Thread.currentThread();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r4.executorThread = r2;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r2 = r4.loadable;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r2 = r2.isLoadCanceled();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            if (r2 != 0) goto L_0x003c;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
        L_0x0010:
            r2 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r2.<init>();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r3 = "load:";	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r2.append(r3);	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r3 = r4.loadable;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r3 = r3.getClass();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r3 = r3.getSimpleName();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r2.append(r3);	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r2 = r2.toString();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            org.telegram.messenger.exoplayer2.util.TraceUtil.beginSection(r2);	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            r2 = r4.loadable;	 Catch:{ all -> 0x0037 }
            r2.load();	 Catch:{ all -> 0x0037 }
            org.telegram.messenger.exoplayer2.util.TraceUtil.endSection();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            goto L_0x003c;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
        L_0x0037:
            r2 = move-exception;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            org.telegram.messenger.exoplayer2.util.TraceUtil.endSection();	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            throw r2;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
        L_0x003c:
            r2 = r4.released;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            if (r2 != 0) goto L_0x00a8;	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
        L_0x0040:
            r4.sendEmptyMessage(r0);	 Catch:{ IOException -> 0x009c, InterruptedException -> 0x008b, Exception -> 0x0072, OutOfMemoryError -> 0x0059, Error -> 0x0044 }
            goto L_0x00a8;
        L_0x0044:
            r0 = move-exception;
            r1 = "LoadTask";
            r2 = "Unexpected error loading stream";
            android.util.Log.e(r1, r2, r0);
            r1 = r4.released;
            if (r1 != 0) goto L_0x0058;
        L_0x0050:
            r1 = 4;
            r1 = r4.obtainMessage(r1, r0);
            r1.sendToTarget();
        L_0x0058:
            throw r0;
        L_0x0059:
            r0 = move-exception;
            r2 = "LoadTask";
            r3 = "OutOfMemory error loading stream";
            android.util.Log.e(r2, r3, r0);
            r2 = r4.released;
            if (r2 != 0) goto L_0x00a8;
        L_0x0065:
            r2 = new org.telegram.messenger.exoplayer2.upstream.Loader$UnexpectedLoaderException;
            r2.<init>(r0);
            r0 = r4.obtainMessage(r1, r2);
            r0.sendToTarget();
            goto L_0x00a8;
        L_0x0072:
            r0 = move-exception;
            r2 = "LoadTask";
            r3 = "Unexpected exception loading stream";
            android.util.Log.e(r2, r3, r0);
            r2 = r4.released;
            if (r2 != 0) goto L_0x00a8;
        L_0x007e:
            r2 = new org.telegram.messenger.exoplayer2.upstream.Loader$UnexpectedLoaderException;
            r2.<init>(r0);
            r0 = r4.obtainMessage(r1, r2);
            r0.sendToTarget();
            goto L_0x00a8;
        L_0x008b:
            r1 = r4.loadable;
            r1 = r1.isLoadCanceled();
            org.telegram.messenger.exoplayer2.util.Assertions.checkState(r1);
            r1 = r4.released;
            if (r1 != 0) goto L_0x00a8;
        L_0x0098:
            r4.sendEmptyMessage(r0);
            goto L_0x00a8;
        L_0x009c:
            r0 = move-exception;
            r2 = r4.released;
            if (r2 != 0) goto L_0x00a8;
        L_0x00a1:
            r0 = r4.obtainMessage(r1, r0);
            r0.sendToTarget();
        L_0x00a8:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.Loader.LoadTask.run():void");
        }

        public void handleMessage(Message message) {
            if (!this.released) {
                if (message.what == 0) {
                    execute();
                } else if (message.what == 4) {
                    throw ((Error) message.obj);
                } else {
                    finish();
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long j = elapsedRealtime - this.startTimeMs;
                    if (this.loadable.isLoadCanceled()) {
                        this.callback.onLoadCanceled(this.loadable, elapsedRealtime, j, false);
                        return;
                    }
                    switch (message.what) {
                        case 1:
                            this.callback.onLoadCanceled(this.loadable, elapsedRealtime, j, false);
                            break;
                        case 2:
                            try {
                                this.callback.onLoadCompleted(this.loadable, elapsedRealtime, j);
                                break;
                            } catch (Message message2) {
                                Log.e(TAG, "Unexpected exception handling load completed", message2);
                                Loader.this.fatalError = new UnexpectedLoaderException(message2);
                                break;
                            }
                        case 3:
                            this.currentError = (IOException) message2.obj;
                            message2 = this.callback.onLoadError(this.loadable, elapsedRealtime, j, this.currentError);
                            if (message2 != 3) {
                                if (message2 != 2) {
                                    int i = 1;
                                    if (message2 != 1) {
                                        i = 1 + this.errorCount;
                                    }
                                    this.errorCount = i;
                                    start(getRetryDelayMillis());
                                    break;
                                }
                            }
                            Loader.this.fatalError = this.currentError;
                            break;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        private void execute() {
            this.currentError = null;
            Loader.this.downloadExecutorService.execute(Loader.this.currentTask);
        }

        private void finish() {
            Loader.this.currentTask = null;
        }

        private long getRetryDelayMillis() {
            return (long) Math.min((this.errorCount - 1) * 1000, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        }
    }

    public interface Loadable {
        void cancelLoad();

        boolean isLoadCanceled();

        void load() throws IOException, InterruptedException;
    }

    public interface ReleaseCallback {
        void onLoaderReleased();
    }

    private static final class ReleaseTask extends Handler implements Runnable {
        private final ReleaseCallback callback;

        public ReleaseTask(ReleaseCallback releaseCallback) {
            this.callback = releaseCallback;
        }

        public void run() {
            if (getLooper().getThread().isAlive()) {
                sendEmptyMessage(0);
            }
        }

        public void handleMessage(Message message) {
            this.callback.onLoaderReleased();
        }
    }

    public static final class UnexpectedLoaderException extends IOException {
        public UnexpectedLoaderException(Throwable th) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected ");
            stringBuilder.append(th.getClass().getSimpleName());
            stringBuilder.append(": ");
            stringBuilder.append(th.getMessage());
            super(stringBuilder.toString(), th);
        }
    }

    public Loader(String str) {
        this.downloadExecutorService = Util.newSingleThreadExecutor(str);
    }

    public <T extends Loadable> long startLoading(T t, Callback<T> callback, int i) {
        Looper myLooper = Looper.myLooper();
        Assertions.checkState(myLooper != null);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        new LoadTask(myLooper, t, callback, i, elapsedRealtime).start(null);
        return elapsedRealtime;
    }

    public boolean isLoading() {
        return this.currentTask != null;
    }

    public void cancelLoading() {
        this.currentTask.cancel(false);
    }

    public void release() {
        release(null);
    }

    public boolean release(ReleaseCallback releaseCallback) {
        boolean z = true;
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            if (releaseCallback != null) {
                this.downloadExecutorService.execute(new ReleaseTask(releaseCallback));
            }
        } else if (releaseCallback != null) {
            releaseCallback.onLoaderReleased();
            this.downloadExecutorService.shutdown();
            return z;
        }
        z = false;
        this.downloadExecutorService.shutdown();
        return z;
    }

    public void maybeThrowError() throws IOException {
        maybeThrowError(Integer.MIN_VALUE);
    }

    public void maybeThrowError(int i) throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        } else if (this.currentTask != null) {
            LoadTask loadTask = this.currentTask;
            if (i == Integer.MIN_VALUE) {
                i = this.currentTask.defaultMinRetryCount;
            }
            loadTask.maybeThrowError(i);
        }
    }
}

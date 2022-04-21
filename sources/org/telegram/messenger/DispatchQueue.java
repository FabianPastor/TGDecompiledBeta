package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {
    private static int indexPointer = 0;
    private volatile Handler handler;
    public final int index;
    private long lastTaskTime;
    private CountDownLatch syncLatch;

    public DispatchQueue(String threadName) {
        this(threadName, true);
    }

    public DispatchQueue(String threadName, boolean start) {
        this.handler = null;
        this.syncLatch = new CountDownLatch(1);
        int i = indexPointer;
        indexPointer = i + 1;
        this.index = i;
        setName(threadName);
        if (start) {
            start();
        }
    }

    public void sendMessage(Message msg, int delay) {
        try {
            this.syncLatch.await();
            if (delay <= 0) {
                this.handler.sendMessage(msg);
            } else {
                this.handler.sendMessageDelayed(msg, (long) delay);
            }
        } catch (Exception e) {
        }
    }

    public void cancelRunnable(Runnable runnable) {
        try {
            this.syncLatch.await();
            this.handler.removeCallbacks(runnable);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void cancelRunnables(Runnable[] runnables) {
        try {
            this.syncLatch.await();
            for (Runnable removeCallbacks : runnables) {
                this.handler.removeCallbacks(removeCallbacks);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean postRunnable(Runnable runnable) {
        this.lastTaskTime = SystemClock.elapsedRealtime();
        return postRunnable(runnable, 0);
    }

    public boolean postRunnable(Runnable runnable, long delay) {
        try {
            this.syncLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (delay <= 0) {
            return this.handler.post(runnable);
        }
        return this.handler.postDelayed(runnable, delay);
    }

    public void cleanupQueue() {
        try {
            this.syncLatch.await();
            this.handler.removeCallbacksAndMessages((Object) null);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void handleMessage(Message inputMessage) {
    }

    public long getLastTaskTime() {
        return this.lastTaskTime;
    }

    public void recycle() {
        this.handler.getLooper().quit();
    }

    public void run() {
        Looper.prepare();
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                DispatchQueue.this.handleMessage(msg);
            }
        };
        this.syncLatch.countDown();
        Looper.loop();
    }

    public boolean isReady() {
        return this.syncLatch.getCount() == 0;
    }
}

package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {
    private volatile Handler handler;
    private long lastTaskTime;
    private CountDownLatch syncLatch;

    public void handleMessage(Message message) {
    }

    public DispatchQueue(String str) {
        this(str, true);
    }

    public DispatchQueue(String str, boolean z) {
        this.handler = null;
        this.syncLatch = new CountDownLatch(1);
        setName(str);
        if (z) {
            start();
        }
    }

    public void sendMessage(Message message, int i) {
        try {
            this.syncLatch.await();
            if (i <= 0) {
                this.handler.sendMessage(message);
            } else {
                this.handler.sendMessageDelayed(message, (long) i);
            }
        } catch (Exception unused) {
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

    public void cancelRunnables(Runnable[] runnableArr) {
        try {
            this.syncLatch.await();
            for (Runnable removeCallbacks : runnableArr) {
                this.handler.removeCallbacks(removeCallbacks);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void postRunnable(Runnable runnable) {
        postRunnable(runnable, 0);
        this.lastTaskTime = SystemClock.elapsedRealtime();
    }

    public void postRunnable(Runnable runnable, long j) {
        try {
            this.syncLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (j <= 0) {
            this.handler.post(runnable);
        } else {
            this.handler.postDelayed(runnable, j);
        }
    }

    public void cleanupQueue() {
        try {
            this.syncLatch.await();
            this.handler.removeCallbacksAndMessages((Object) null);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
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
            public void handleMessage(Message message) {
                DispatchQueue.this.handleMessage(message);
            }
        };
        this.syncLatch.countDown();
        Looper.loop();
    }
}

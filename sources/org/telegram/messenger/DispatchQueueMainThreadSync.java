package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import java.util.ArrayList;

public class DispatchQueueMainThreadSync extends Thread {
    private static int indexPointer = 0;
    private volatile Handler handler;
    public final int index;
    private boolean isRecycled;
    /* access modifiers changed from: private */
    public boolean isRunning;
    private long lastTaskTime;
    /* access modifiers changed from: private */
    public ArrayList<PostponedTask> postponedTasks;

    public DispatchQueueMainThreadSync(String threadName) {
        this(threadName, true);
    }

    public DispatchQueueMainThreadSync(String threadName, boolean start) {
        this.handler = null;
        int i = indexPointer;
        indexPointer = i + 1;
        this.index = i;
        this.postponedTasks = new ArrayList<>();
        setName(threadName);
        if (start) {
            start();
        }
    }

    public void sendMessage(Message msg, int delay) {
        checkThread();
        if (!this.isRecycled) {
            if (!this.isRunning) {
                this.postponedTasks.add(new PostponedTask(msg, delay));
            } else if (delay <= 0) {
                this.handler.sendMessage(msg);
            } else {
                this.handler.sendMessageDelayed(msg, (long) delay);
            }
        }
    }

    private void checkThread() {
        if (BuildVars.DEBUG_PRIVATE_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new IllegalStateException("Disaptch thread");
        }
    }

    public void cancelRunnable(Runnable runnable) {
        checkThread();
        if (this.isRunning) {
            this.handler.removeCallbacks(runnable);
            return;
        }
        int i = 0;
        while (i < this.postponedTasks.size()) {
            if (this.postponedTasks.get(i).runnable == runnable) {
                this.postponedTasks.remove(i);
                i--;
            }
            i++;
        }
    }

    public void cancelRunnables(Runnable[] runnables) {
        checkThread();
        for (Runnable cancelRunnable : runnables) {
            cancelRunnable(cancelRunnable);
        }
    }

    public boolean postRunnable(Runnable runnable) {
        checkThread();
        this.lastTaskTime = SystemClock.elapsedRealtime();
        return postRunnable(runnable, 0);
    }

    public boolean postRunnable(Runnable runnable, long delay) {
        checkThread();
        if (this.isRecycled) {
            return false;
        }
        if (!this.isRunning) {
            this.postponedTasks.add(new PostponedTask(runnable, delay));
            return true;
        } else if (delay <= 0) {
            return this.handler.post(runnable);
        } else {
            return this.handler.postDelayed(runnable, delay);
        }
    }

    public void cleanupQueue() {
        checkThread();
        this.postponedTasks.clear();
        this.handler.removeCallbacksAndMessages((Object) null);
    }

    public void handleMessage(Message inputMessage) {
    }

    public long getLastTaskTime() {
        return this.lastTaskTime;
    }

    public void recycle() {
        checkThread();
        postRunnable(new DispatchQueueMainThreadSync$$ExternalSyntheticLambda1(this));
        this.isRecycled = true;
    }

    /* renamed from: lambda$recycle$0$org-telegram-messenger-DispatchQueueMainThreadSync  reason: not valid java name */
    public /* synthetic */ void m1796x30a1b77a() {
        this.handler.getLooper().quit();
    }

    public void run() {
        Looper.prepare();
        this.handler = new Handler(Looper.myLooper(), new DispatchQueueMainThreadSync$$ExternalSyntheticLambda0(this));
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                boolean unused = DispatchQueueMainThreadSync.this.isRunning = true;
                for (int i = 0; i < DispatchQueueMainThreadSync.this.postponedTasks.size(); i++) {
                    ((PostponedTask) DispatchQueueMainThreadSync.this.postponedTasks.get(i)).run();
                }
                DispatchQueueMainThreadSync.this.postponedTasks.clear();
            }
        });
        Looper.loop();
    }

    /* renamed from: lambda$run$1$org-telegram-messenger-DispatchQueueMainThreadSync  reason: not valid java name */
    public /* synthetic */ boolean m1797lambda$run$1$orgtelegrammessengerDispatchQueueMainThreadSync(Message msg) {
        handleMessage(msg);
        return true;
    }

    public boolean isReady() {
        return this.isRunning;
    }

    public Handler getHandler() {
        return this.handler;
    }

    private class PostponedTask {
        long delay;
        Message message;
        Runnable runnable;

        public PostponedTask(Message msg, int delay2) {
            this.message = msg;
            this.delay = (long) delay2;
        }

        public PostponedTask(Runnable runnable2, long delay2) {
            this.runnable = runnable2;
            this.delay = delay2;
        }

        public void run() {
            Runnable runnable2 = this.runnable;
            if (runnable2 != null) {
                DispatchQueueMainThreadSync.this.postRunnable(runnable2, this.delay);
            } else {
                DispatchQueueMainThreadSync.this.sendMessage(this.message, (int) this.delay);
            }
        }
    }
}

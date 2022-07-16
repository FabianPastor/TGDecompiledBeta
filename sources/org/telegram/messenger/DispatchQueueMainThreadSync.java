package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import java.util.ArrayList;

public class DispatchQueueMainThreadSync extends Thread {
    private static int indexPointer;
    private volatile Handler handler;
    public final int index;
    private boolean isRecycled;
    /* access modifiers changed from: private */
    public boolean isRunning;
    private long lastTaskTime;
    /* access modifiers changed from: private */
    public ArrayList<PostponedTask> postponedTasks;

    public void handleMessage(Message message) {
    }

    public DispatchQueueMainThreadSync(String str) {
        this(str, true);
    }

    public DispatchQueueMainThreadSync(String str, boolean z) {
        this.handler = null;
        int i = indexPointer;
        indexPointer = i + 1;
        this.index = i;
        this.postponedTasks = new ArrayList<>();
        setName(str);
        if (z) {
            start();
        }
    }

    public void sendMessage(Message message, int i) {
        checkThread();
        if (!this.isRecycled) {
            if (!this.isRunning) {
                this.postponedTasks.add(new PostponedTask(message, i));
            } else if (i <= 0) {
                this.handler.sendMessage(message);
            } else {
                this.handler.sendMessageDelayed(message, (long) i);
            }
        }
    }

    private void checkThread() {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            Thread.currentThread();
            ApplicationLoader.applicationHandler.getLooper().getThread();
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

    public void cancelRunnables(Runnable[] runnableArr) {
        checkThread();
        for (Runnable cancelRunnable : runnableArr) {
            cancelRunnable(cancelRunnable);
        }
    }

    public boolean postRunnable(Runnable runnable) {
        checkThread();
        this.lastTaskTime = SystemClock.elapsedRealtime();
        return postRunnable(runnable, 0);
    }

    public boolean postRunnable(Runnable runnable, long j) {
        checkThread();
        if (this.isRecycled) {
            return false;
        }
        if (!this.isRunning) {
            this.postponedTasks.add(new PostponedTask(runnable, j));
            return true;
        } else if (j <= 0) {
            return this.handler.post(runnable);
        } else {
            return this.handler.postDelayed(runnable, j);
        }
    }

    public void cleanupQueue() {
        checkThread();
        this.postponedTasks.clear();
        this.handler.removeCallbacksAndMessages((Object) null);
    }

    public long getLastTaskTime() {
        return this.lastTaskTime;
    }

    public void recycle() {
        checkThread();
        postRunnable(new DispatchQueueMainThreadSync$$ExternalSyntheticLambda1(this));
        this.isRecycled = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$recycle$0() {
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$run$1(Message message) {
        handleMessage(message);
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

        public PostponedTask(Message message2, int i) {
            this.message = message2;
            this.delay = (long) i;
        }

        public PostponedTask(Runnable runnable2, long j) {
            this.runnable = runnable2;
            this.delay = j;
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

package org.telegram;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class DispatchQueuePriority {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new PriorityBlockingQueue(10, new Comparator<Runnable>(this) { // from class: org.telegram.DispatchQueuePriority.1
        @Override // java.util.Comparator
        public int compare(Runnable runnable, Runnable runnable2) {
            int i = 1;
            int i2 = runnable instanceof PriorityRunnable ? ((PriorityRunnable) runnable).priority : 1;
            if (runnable2 instanceof PriorityRunnable) {
                i = ((PriorityRunnable) runnable2).priority;
            }
            return i - i2;
        }
    }));

    public DispatchQueuePriority(String str) {
    }

    public void postRunnable(Runnable runnable) {
        this.threadPoolExecutor.execute(runnable);
    }

    public Runnable postRunnable(Runnable runnable, int i) {
        if (i == 1) {
            postRunnable(runnable);
            return runnable;
        }
        PriorityRunnable priorityRunnable = new PriorityRunnable(i, runnable);
        this.threadPoolExecutor.execute(priorityRunnable);
        return priorityRunnable;
    }

    public void cancelRunnable(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        this.threadPoolExecutor.remove(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PriorityRunnable implements Runnable {
        final int priority;
        final Runnable runnable;

        private PriorityRunnable(int i, Runnable runnable) {
            this.priority = i;
            this.runnable = runnable;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.runnable.run();
        }
    }
}

package org.telegram.messenger;

import android.os.SystemClock;
import java.util.HashMap;
import java.util.LinkedList;

public class DispatchQueuePool {
    /* access modifiers changed from: private */
    public LinkedList<DispatchQueue> busyQueues = new LinkedList<>();
    private HashMap<DispatchQueue, Integer> busyQueuesMap = new HashMap<>();
    private Runnable cleanupRunnable = new Runnable() {
        public void run() {
            if (!DispatchQueuePool.this.queues.isEmpty()) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                int size = DispatchQueuePool.this.queues.size();
                int i = 0;
                while (i < size) {
                    DispatchQueue dispatchQueue = (DispatchQueue) DispatchQueuePool.this.queues.get(i);
                    if (dispatchQueue.getLastTaskTime() < elapsedRealtime - 30000) {
                        dispatchQueue.recycle();
                        DispatchQueuePool.this.queues.remove(i);
                        DispatchQueuePool.access$110(DispatchQueuePool.this);
                        i--;
                        size--;
                    }
                    i++;
                }
            }
            if (!DispatchQueuePool.this.queues.isEmpty() || !DispatchQueuePool.this.busyQueues.isEmpty()) {
                AndroidUtilities.runOnUIThread(this, 30000);
                boolean unused = DispatchQueuePool.this.cleanupScheduled = true;
                return;
            }
            boolean unused2 = DispatchQueuePool.this.cleanupScheduled = false;
        }
    };
    /* access modifiers changed from: private */
    public boolean cleanupScheduled;
    private int createdCount;
    private int guid;
    private int maxCount;
    /* access modifiers changed from: private */
    public LinkedList<DispatchQueue> queues = new LinkedList<>();
    private int totalTasksCount;

    static /* synthetic */ int access$110(DispatchQueuePool dispatchQueuePool) {
        int i = dispatchQueuePool.createdCount;
        dispatchQueuePool.createdCount = i - 1;
        return i;
    }

    public DispatchQueuePool(int i) {
        this.maxCount = i;
        this.guid = Utilities.random.nextInt();
    }

    public void execute(Runnable runnable) {
        DispatchQueue dispatchQueue;
        if (!this.busyQueues.isEmpty() && (this.totalTasksCount / 2 <= this.busyQueues.size() || (this.queues.isEmpty() && this.createdCount >= this.maxCount))) {
            dispatchQueue = this.busyQueues.remove(0);
        } else if (this.queues.isEmpty()) {
            dispatchQueue = new DispatchQueue("DispatchQueuePool" + this.guid + "_" + Utilities.random.nextInt());
            dispatchQueue.setPriority(10);
            this.createdCount = this.createdCount + 1;
        } else {
            dispatchQueue = this.queues.remove(0);
        }
        if (!this.cleanupScheduled) {
            AndroidUtilities.runOnUIThread(this.cleanupRunnable, 30000);
            this.cleanupScheduled = true;
        }
        this.totalTasksCount++;
        this.busyQueues.add(dispatchQueue);
        Integer num = this.busyQueuesMap.get(dispatchQueue);
        if (num == null) {
            num = 0;
        }
        this.busyQueuesMap.put(dispatchQueue, Integer.valueOf(num.intValue() + 1));
        dispatchQueue.postRunnable(new Runnable(runnable, dispatchQueue) {
            public final /* synthetic */ Runnable f$1;
            public final /* synthetic */ DispatchQueue f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DispatchQueuePool.this.lambda$execute$1$DispatchQueuePool(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$execute$1$DispatchQueuePool(Runnable runnable, DispatchQueue dispatchQueue) {
        runnable.run();
        AndroidUtilities.runOnUIThread(new Runnable(dispatchQueue) {
            public final /* synthetic */ DispatchQueue f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DispatchQueuePool.this.lambda$null$0$DispatchQueuePool(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$DispatchQueuePool(DispatchQueue dispatchQueue) {
        this.totalTasksCount--;
        int intValue = this.busyQueuesMap.get(dispatchQueue).intValue() - 1;
        if (intValue == 0) {
            this.busyQueuesMap.remove(dispatchQueue);
            this.busyQueues.remove(dispatchQueue);
            this.queues.add(dispatchQueue);
            return;
        }
        this.busyQueuesMap.put(dispatchQueue, Integer.valueOf(intValue));
    }
}

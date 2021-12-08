package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseIntArray;
import java.util.LinkedList;

public class DispatchQueuePool {
    /* access modifiers changed from: private */
    public LinkedList<DispatchQueue> busyQueues = new LinkedList<>();
    private SparseIntArray busyQueuesMap = new SparseIntArray();
    private Runnable cleanupRunnable = new Runnable() {
        public void run() {
            if (!DispatchQueuePool.this.queues.isEmpty()) {
                long currentTime = SystemClock.elapsedRealtime();
                int a = 0;
                int N = DispatchQueuePool.this.queues.size();
                while (a < N) {
                    DispatchQueue queue = (DispatchQueue) DispatchQueuePool.this.queues.get(a);
                    if (queue.getLastTaskTime() < currentTime - 30000) {
                        queue.recycle();
                        DispatchQueuePool.this.queues.remove(a);
                        DispatchQueuePool.access$110(DispatchQueuePool.this);
                        a--;
                        N--;
                    }
                    a++;
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

    static /* synthetic */ int access$110(DispatchQueuePool x0) {
        int i = x0.createdCount;
        x0.createdCount = i - 1;
        return i;
    }

    public DispatchQueuePool(int count) {
        this.maxCount = count;
        this.guid = Utilities.random.nextInt();
    }

    public void execute(Runnable runnable) {
        DispatchQueue queue;
        if (!this.busyQueues.isEmpty() && (this.totalTasksCount / 2 <= this.busyQueues.size() || (this.queues.isEmpty() && this.createdCount >= this.maxCount))) {
            queue = this.busyQueues.remove(0);
        } else if (this.queues.isEmpty()) {
            queue = new DispatchQueue("DispatchQueuePool" + this.guid + "_" + Utilities.random.nextInt());
            queue.setPriority(10);
            this.createdCount = this.createdCount + 1;
        } else {
            queue = this.queues.remove(0);
        }
        if (!this.cleanupScheduled) {
            AndroidUtilities.runOnUIThread(this.cleanupRunnable, 30000);
            this.cleanupScheduled = true;
        }
        this.totalTasksCount++;
        this.busyQueues.add(queue);
        this.busyQueuesMap.put(queue.index, this.busyQueuesMap.get(queue.index, 0) + 1);
        queue.postRunnable(new DispatchQueuePool$$ExternalSyntheticLambda0(this, runnable, queue));
    }

    /* renamed from: lambda$execute$1$org-telegram-messenger-DispatchQueuePool  reason: not valid java name */
    public /* synthetic */ void m616lambda$execute$1$orgtelegrammessengerDispatchQueuePool(Runnable runnable, DispatchQueue queue) {
        runnable.run();
        AndroidUtilities.runOnUIThread(new DispatchQueuePool$$ExternalSyntheticLambda1(this, queue));
    }

    /* renamed from: lambda$execute$0$org-telegram-messenger-DispatchQueuePool  reason: not valid java name */
    public /* synthetic */ void m615lambda$execute$0$orgtelegrammessengerDispatchQueuePool(DispatchQueue queue) {
        this.totalTasksCount--;
        int remainingTasksCount = this.busyQueuesMap.get(queue.index) - 1;
        if (remainingTasksCount == 0) {
            this.busyQueuesMap.delete(queue.index);
            this.busyQueues.remove(queue);
            this.queues.add(queue);
            return;
        }
        this.busyQueuesMap.put(queue.index, remainingTasksCount);
    }
}

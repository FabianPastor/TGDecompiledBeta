package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseIntArray;
import java.util.LinkedList;

public class DispatchQueuePoolMainThreadSync {
    /* access modifiers changed from: private */
    public LinkedList<DispatchQueueMainThreadSync> busyQueues = new LinkedList<>();
    private SparseIntArray busyQueuesMap = new SparseIntArray();
    private Runnable cleanupRunnable = new Runnable() {
        public void run() {
            if (!DispatchQueuePoolMainThreadSync.this.queues.isEmpty()) {
                long currentTime = SystemClock.elapsedRealtime();
                int a = 0;
                int N = DispatchQueuePoolMainThreadSync.this.queues.size();
                while (a < N) {
                    DispatchQueueMainThreadSync queue = (DispatchQueueMainThreadSync) DispatchQueuePoolMainThreadSync.this.queues.get(a);
                    if (queue.getLastTaskTime() < currentTime - 30000) {
                        queue.recycle();
                        DispatchQueuePoolMainThreadSync.this.queues.remove(a);
                        DispatchQueuePoolMainThreadSync.access$110(DispatchQueuePoolMainThreadSync.this);
                        a--;
                        N--;
                    }
                    a++;
                }
            }
            if (!DispatchQueuePoolMainThreadSync.this.queues.isEmpty() || !DispatchQueuePoolMainThreadSync.this.busyQueues.isEmpty()) {
                AndroidUtilities.runOnUIThread(this, 30000);
                boolean unused = DispatchQueuePoolMainThreadSync.this.cleanupScheduled = true;
                return;
            }
            boolean unused2 = DispatchQueuePoolMainThreadSync.this.cleanupScheduled = false;
        }
    };
    /* access modifiers changed from: private */
    public boolean cleanupScheduled;
    private int createdCount;
    private int guid;
    private int maxCount;
    /* access modifiers changed from: private */
    public LinkedList<DispatchQueueMainThreadSync> queues = new LinkedList<>();
    private int totalTasksCount;

    static /* synthetic */ int access$110(DispatchQueuePoolMainThreadSync x0) {
        int i = x0.createdCount;
        x0.createdCount = i - 1;
        return i;
    }

    public DispatchQueuePoolMainThreadSync(int count) {
        this.maxCount = count;
        this.guid = Utilities.random.nextInt();
    }

    public void execute(Runnable runnable) {
        DispatchQueueMainThreadSync queue;
        if (!this.busyQueues.isEmpty() && (this.totalTasksCount / 2 <= this.busyQueues.size() || (this.queues.isEmpty() && this.createdCount >= this.maxCount))) {
            queue = this.busyQueues.remove(0);
        } else if (this.queues.isEmpty()) {
            queue = new DispatchQueueMainThreadSync("DispatchQueuePool" + this.guid + "_" + Utilities.random.nextInt());
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
        queue.postRunnable(new DispatchQueuePoolMainThreadSync$$ExternalSyntheticLambda0(this, runnable, queue));
    }

    /* renamed from: lambda$execute$1$org-telegram-messenger-DispatchQueuePoolMainThreadSync  reason: not valid java name */
    public /* synthetic */ void m1801xa66884f3(Runnable runnable, DispatchQueueMainThreadSync queue) {
        runnable.run();
        AndroidUtilities.runOnUIThread(new DispatchQueuePoolMainThreadSync$$ExternalSyntheticLambda1(this, queue));
    }

    /* renamed from: lambda$execute$0$org-telegram-messenger-DispatchQueuePoolMainThreadSync  reason: not valid java name */
    public /* synthetic */ void m1800xa5323214(DispatchQueueMainThreadSync queue) {
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

package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseIntArray;
import java.util.ArrayList;

public class DispatchQueuePoolBackground {
    private static DispatchQueuePoolBackground backgroundQueue;
    private static final Runnable finishCollectUpdateRunnable = new Runnable() {
        public void run() {
            DispatchQueuePoolBackground.finishCollectUpdateRunnables();
        }
    };
    private static final ArrayList<ArrayList<Runnable>> freeCollections = new ArrayList<>();
    static ArrayList<Runnable> updateTaskCollection;
    /* access modifiers changed from: private */
    public ArrayList<DispatchQueue> busyQueues = new ArrayList<>(10);
    private SparseIntArray busyQueuesMap = new SparseIntArray();
    private Runnable cleanupRunnable = new Runnable() {
        public void run() {
            if (!DispatchQueuePoolBackground.this.queues.isEmpty()) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                int i = 0;
                while (i < DispatchQueuePoolBackground.this.queues.size()) {
                    DispatchQueue dispatchQueue = (DispatchQueue) DispatchQueuePoolBackground.this.queues.get(i);
                    if (dispatchQueue.getLastTaskTime() < elapsedRealtime - 30000) {
                        dispatchQueue.recycle();
                        DispatchQueuePoolBackground.this.queues.remove(i);
                        DispatchQueuePoolBackground.access$110(DispatchQueuePoolBackground.this);
                        i--;
                    }
                    i++;
                }
            }
            if (!DispatchQueuePoolBackground.this.queues.isEmpty() || !DispatchQueuePoolBackground.this.busyQueues.isEmpty()) {
                AndroidUtilities.runOnUIThread(this, 30000);
                boolean unused = DispatchQueuePoolBackground.this.cleanupScheduled = true;
                return;
            }
            boolean unused2 = DispatchQueuePoolBackground.this.cleanupScheduled = false;
        }
    };
    /* access modifiers changed from: private */
    public boolean cleanupScheduled;
    private int createdCount;
    private int guid;
    private int maxCount;
    /* access modifiers changed from: private */
    public ArrayList<DispatchQueue> queues = new ArrayList<>(10);
    private int totalTasksCount;

    static /* synthetic */ int access$110(DispatchQueuePoolBackground dispatchQueuePoolBackground) {
        int i = dispatchQueuePoolBackground.createdCount;
        dispatchQueuePoolBackground.createdCount = i - 1;
        return i;
    }

    private DispatchQueuePoolBackground(int i) {
        this.maxCount = i;
        this.guid = Utilities.random.nextInt();
    }

    private void execute(ArrayList<Runnable> arrayList) {
        DispatchQueue dispatchQueue;
        for (int i = 0; i < arrayList.size(); i++) {
            Runnable runnable = arrayList.get(i);
            if (runnable != null) {
                if (!this.busyQueues.isEmpty() && (this.totalTasksCount / 2 <= this.busyQueues.size() || (this.queues.isEmpty() && this.createdCount >= this.maxCount))) {
                    dispatchQueue = this.busyQueues.remove(0);
                } else if (this.queues.isEmpty()) {
                    dispatchQueue = new DispatchQueue("DispatchQueuePoolThreadSafety" + this.guid + "_" + Utilities.random.nextInt());
                    dispatchQueue.setPriority(10);
                    this.createdCount = this.createdCount + 1;
                } else {
                    dispatchQueue = this.queues.remove(0);
                }
                if (!this.cleanupScheduled) {
                    Utilities.globalQueue.postRunnable(this.cleanupRunnable, 30000);
                    this.cleanupScheduled = true;
                }
                this.totalTasksCount++;
                this.busyQueues.add(dispatchQueue);
                this.busyQueuesMap.put(dispatchQueue.index, this.busyQueuesMap.get(dispatchQueue.index, 0) + 1);
                dispatchQueue.postRunnable(new DispatchQueuePoolBackground$$ExternalSyntheticLambda2(this, runnable, dispatchQueue));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$execute$1(Runnable runnable, DispatchQueue dispatchQueue) {
        runnable.run();
        Utilities.globalQueue.postRunnable(new DispatchQueuePoolBackground$$ExternalSyntheticLambda3(this, dispatchQueue));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$execute$0(DispatchQueue dispatchQueue) {
        this.totalTasksCount--;
        int i = this.busyQueuesMap.get(dispatchQueue.index) - 1;
        if (i == 0) {
            this.busyQueuesMap.delete(dispatchQueue.index);
            this.busyQueues.remove(dispatchQueue);
            this.queues.add(dispatchQueue);
            return;
        }
        this.busyQueuesMap.put(dispatchQueue.index, i);
    }

    public static void execute(Runnable runnable) {
        if (!BuildVars.DEBUG_PRIVATE_VERSION || Thread.currentThread() == ApplicationLoader.applicationHandler.getLooper().getThread()) {
            if (updateTaskCollection == null) {
                ArrayList<ArrayList<Runnable>> arrayList = freeCollections;
                if (!arrayList.isEmpty()) {
                    updateTaskCollection = arrayList.remove(arrayList.size() - 1);
                } else {
                    updateTaskCollection = new ArrayList<>(100);
                }
                AndroidUtilities.runOnUIThread(finishCollectUpdateRunnable);
            }
            updateTaskCollection.add(runnable);
            return;
        }
        throw new RuntimeException("wrong thread");
    }

    /* access modifiers changed from: private */
    public static void finishCollectUpdateRunnables() {
        ArrayList<Runnable> arrayList = updateTaskCollection;
        if (arrayList == null || arrayList.isEmpty()) {
            updateTaskCollection = null;
            return;
        }
        ArrayList<Runnable> arrayList2 = updateTaskCollection;
        updateTaskCollection = null;
        if (backgroundQueue == null) {
            backgroundQueue = new DispatchQueuePoolBackground(Math.max(1, Runtime.getRuntime().availableProcessors() - 2));
        }
        Utilities.globalQueue.postRunnable(new DispatchQueuePoolBackground$$ExternalSyntheticLambda0(arrayList2));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$finishCollectUpdateRunnables$3(ArrayList arrayList) {
        backgroundQueue.execute((ArrayList<Runnable>) arrayList);
        arrayList.clear();
        AndroidUtilities.runOnUIThread(new DispatchQueuePoolBackground$$ExternalSyntheticLambda1(arrayList));
    }
}

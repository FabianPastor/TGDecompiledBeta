package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseIntArray;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class DispatchQueuePoolBackground {
    private static DispatchQueuePoolBackground backgroundQueue;
    static ArrayList<Runnable> updateTaskCollection;
    private boolean cleanupScheduled;
    private int createdCount;
    private int maxCount;
    private int totalTasksCount;
    private static final ArrayList<ArrayList<Runnable>> freeCollections = new ArrayList<>();
    private static final Runnable finishCollectUpdateRunnable = new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolBackground.2
        @Override // java.lang.Runnable
        public void run() {
            DispatchQueuePoolBackground.finishCollectUpdateRunnables();
        }
    };
    private ArrayList<DispatchQueue> queues = new ArrayList<>(10);
    private SparseIntArray busyQueuesMap = new SparseIntArray();
    private ArrayList<DispatchQueue> busyQueues = new ArrayList<>(10);
    private Runnable cleanupRunnable = new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolBackground.1
        @Override // java.lang.Runnable
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
            if (DispatchQueuePoolBackground.this.queues.isEmpty() && DispatchQueuePoolBackground.this.busyQueues.isEmpty()) {
                DispatchQueuePoolBackground.this.cleanupScheduled = false;
                return;
            }
            Utilities.globalQueue.postRunnable(this, 30000L);
            DispatchQueuePoolBackground.this.cleanupScheduled = true;
        }
    };
    private int guid = Utilities.random.nextInt();

    static /* synthetic */ int access$110(DispatchQueuePoolBackground dispatchQueuePoolBackground) {
        int i = dispatchQueuePoolBackground.createdCount;
        dispatchQueuePoolBackground.createdCount = i - 1;
        return i;
    }

    private DispatchQueuePoolBackground(int i) {
        this.maxCount = i;
    }

    private void execute(ArrayList<Runnable> arrayList) {
        final DispatchQueue remove;
        for (int i = 0; i < arrayList.size(); i++) {
            final Runnable runnable = arrayList.get(i);
            if (runnable != null) {
                if (!this.busyQueues.isEmpty() && (this.totalTasksCount / 2 <= this.busyQueues.size() || (this.queues.isEmpty() && this.createdCount >= this.maxCount))) {
                    remove = this.busyQueues.remove(0);
                } else if (this.queues.isEmpty()) {
                    remove = new DispatchQueue("DispatchQueuePoolThreadSafety" + this.guid + "_" + Utilities.random.nextInt());
                    remove.setPriority(10);
                    this.createdCount = this.createdCount + 1;
                } else {
                    remove = this.queues.remove(0);
                }
                if (!this.cleanupScheduled) {
                    Utilities.globalQueue.postRunnable(this.cleanupRunnable, 30000L);
                    this.cleanupScheduled = true;
                }
                this.totalTasksCount++;
                this.busyQueues.add(remove);
                this.busyQueuesMap.put(remove.index, this.busyQueuesMap.get(remove.index, 0) + 1);
                remove.postRunnable(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolBackground$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        DispatchQueuePoolBackground.this.lambda$execute$1(runnable, remove);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$execute$1(Runnable runnable, final DispatchQueue dispatchQueue) {
        runnable.run();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolBackground$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueuePoolBackground.this.lambda$execute$0(dispatchQueue);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
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
        if (BuildVars.DEBUG_PRIVATE_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("wrong thread");
        }
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
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void finishCollectUpdateRunnables() {
        ArrayList<Runnable> arrayList = updateTaskCollection;
        if (arrayList == null || arrayList.isEmpty()) {
            updateTaskCollection = null;
            return;
        }
        final ArrayList<Runnable> arrayList2 = updateTaskCollection;
        updateTaskCollection = null;
        if (backgroundQueue == null) {
            backgroundQueue = new DispatchQueuePoolBackground(Math.max(1, Runtime.getRuntime().availableProcessors() - 2));
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolBackground$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueuePoolBackground.lambda$finishCollectUpdateRunnables$3(arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$finishCollectUpdateRunnables$3(final ArrayList arrayList) {
        backgroundQueue.execute(arrayList);
        arrayList.clear();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolBackground$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueuePoolBackground.lambda$finishCollectUpdateRunnables$2(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$finishCollectUpdateRunnables$2(ArrayList arrayList) {
        freeCollections.add(arrayList);
    }
}

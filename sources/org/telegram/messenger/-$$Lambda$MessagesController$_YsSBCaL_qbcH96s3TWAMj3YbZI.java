package org.telegram.messenger;

import android.util.LongSparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$_YsSBCaL_qbcH96s3TWAMj3YbZI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ LongSparseArray f$1;
    private final /* synthetic */ LongSparseArray f$2;

    public /* synthetic */ -$$Lambda$MessagesController$_YsSBCaL_qbcH96s3TWAMj3YbZI(MessagesController messagesController, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = longSparseArray;
        this.f$2 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdateRead$133$MessagesController(this.f$1, this.f$2);
    }
}

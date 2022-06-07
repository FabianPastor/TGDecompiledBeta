package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda120 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda120(MessagesStorage messagesStorage, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
        this.f$2 = longSparseArray2;
        this.f$3 = longSparseArray3;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$putChannelViews$153(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

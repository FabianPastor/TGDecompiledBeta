package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda119 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ LongSparseArray f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda119(MessagesStorage messagesStorage, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
        this.f$2 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$getNewTask$80(this.f$1, this.f$2);
    }
}

package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda111 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ LongSparseArray f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda111(MessagesStorage messagesStorage, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
        this.f$2 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$getNewTask$71(this.f$1, this.f$2);
    }
}

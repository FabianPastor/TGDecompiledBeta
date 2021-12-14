package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda110 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ LongSparseArray f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda110(MessagesStorage messagesStorage, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$putWebPages$143(this.f$1);
    }
}

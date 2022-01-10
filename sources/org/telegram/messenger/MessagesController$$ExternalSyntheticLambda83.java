package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda83 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ LongSparseArray f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda83(MessagesController messagesController, LongSparseArray longSparseArray) {
        this.f$0 = messagesController;
        this.f$1 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$303(this.f$1);
    }
}

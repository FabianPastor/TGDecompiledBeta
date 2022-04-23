package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda96 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda96(MessagesController messagesController, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, int i) {
        this.f$0 = messagesController;
        this.f$1 = longSparseArray;
        this.f$2 = longSparseArray2;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDeleteTask$65(this.f$1, this.f$2, this.f$3);
    }
}

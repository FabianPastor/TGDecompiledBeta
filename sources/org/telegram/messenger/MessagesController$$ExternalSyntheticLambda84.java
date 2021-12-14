package org.telegram.messenger;

import androidx.collection.LongSparseArray;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda84 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ LongSparseArray f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda84(MessagesController messagesController, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = longSparseArray;
        this.f$2 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$updatePrintingStrings$130(this.f$1, this.f$2);
    }
}

package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.messenger.support.LongSparseIntArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda158 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ LongSparseIntArray f$1;
    public final /* synthetic */ LongSparseIntArray f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda158(MessagesStorage messagesStorage, LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseIntArray;
        this.f$2 = longSparseIntArray2;
        this.f$3 = longSparseArray;
    }

    public final void run() {
        this.f$0.lambda$updateDialogsWithReadMessages$90(this.f$1, this.f$2, this.f$3);
    }
}

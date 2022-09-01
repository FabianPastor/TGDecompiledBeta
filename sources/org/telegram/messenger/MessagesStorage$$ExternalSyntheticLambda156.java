package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.messenger.support.LongSparseIntArray;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda156 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ LongSparseIntArray f$1;
    public final /* synthetic */ LongSparseIntArray f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ LongSparseIntArray f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda156(MessagesStorage messagesStorage, LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, LongSparseArray longSparseArray, LongSparseIntArray longSparseIntArray3) {
        this.f$0 = messagesStorage;
        this.f$1 = longSparseIntArray;
        this.f$2 = longSparseIntArray2;
        this.f$3 = longSparseArray;
        this.f$4 = longSparseIntArray3;
    }

    public final void run() {
        this.f$0.lambda$updateDialogsWithReadMessages$89(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

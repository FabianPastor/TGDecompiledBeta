package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda95 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.updates_Difference f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda95(MessagesController messagesController, TLRPC.updates_Difference updates_difference, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = updates_difference;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
    }

    public final void run() {
        this.f$0.m219x93925a03(this.f$1, this.f$2, this.f$3);
    }
}

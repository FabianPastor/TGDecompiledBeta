package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC$updates_Difference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda188 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_Difference f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda188(MessagesController messagesController, TLRPC$updates_Difference tLRPC$updates_Difference, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_Difference;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$getDifference$266(this.f$1, this.f$2, this.f$3);
    }
}

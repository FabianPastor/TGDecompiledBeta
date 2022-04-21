package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda76 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_messages_messageViews f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda76(MessagesController messagesController, TLRPC.TL_messages_messageViews tL_messages_messageViews, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        this.f$0 = messagesController;
        this.f$1 = tL_messages_messageViews;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
        this.f$4 = longSparseArray3;
    }

    public final void run() {
        this.f$0.m423x9cd1b302(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

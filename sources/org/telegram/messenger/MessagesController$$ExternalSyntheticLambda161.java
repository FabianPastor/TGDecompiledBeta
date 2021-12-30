package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC$TL_messages_messageViews;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda161 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_messages_messageViews f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda161(MessagesController messagesController, TLRPC$TL_messages_messageViews tLRPC$TL_messages_messageViews, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_messages_messageViews;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
        this.f$4 = longSparseArray3;
    }

    public final void run() {
        this.f$0.lambda$updateTimerProc$116(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.TLRPC$TL_messages_messageViews;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda153 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_messages_messageViews f$1;
    public final /* synthetic */ SparseArray f$2;
    public final /* synthetic */ SparseArray f$3;
    public final /* synthetic */ SparseArray f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda153(MessagesController messagesController, TLRPC$TL_messages_messageViews tLRPC$TL_messages_messageViews, SparseArray sparseArray, SparseArray sparseArray2, SparseArray sparseArray3) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_messages_messageViews;
        this.f$2 = sparseArray;
        this.f$3 = sparseArray2;
        this.f$4 = sparseArray3;
    }

    public final void run() {
        this.f$0.lambda$updateTimerProc$114(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

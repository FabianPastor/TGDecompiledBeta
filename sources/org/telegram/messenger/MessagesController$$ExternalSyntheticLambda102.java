package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda102 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.messages_Dialogs f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda102(MessagesController messagesController, TLRPC.messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
    }

    public final void run() {
        this.f$0.m159x43eb0441(this.f$1, this.f$2, this.f$3);
    }
}

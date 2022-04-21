package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda96 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.messages_Dialogs f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ LongSparseIntArray f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda96(MessagesController messagesController, TLRPC.messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, LongSparseIntArray longSparseIntArray) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
        this.f$4 = z;
        this.f$5 = longSparseIntArray;
    }

    public final void run() {
        this.f$0.m311x2a6b798c(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

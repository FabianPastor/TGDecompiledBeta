package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda264 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC.messages_Dialogs f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ LongSparseArray f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda264(MessagesController messagesController, int i, int i2, int i3, TLRPC.messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = messages_dialogs;
        this.f$5 = longSparseArray;
        this.f$6 = longSparseArray2;
    }

    public final void run() {
        this.f$0.m152x664009a3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

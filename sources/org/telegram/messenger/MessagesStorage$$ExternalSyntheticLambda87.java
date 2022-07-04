package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda87 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.messages_Dialogs f$1;
    public final /* synthetic */ LongSparseArray f$10;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ TLRPC.Message f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ LongSparseArray f$9;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda87(MessagesStorage messagesStorage, TLRPC.messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, TLRPC.Message message, int i6, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesStorage;
        this.f$1 = messages_dialogs;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = i3;
        this.f$5 = i4;
        this.f$6 = i5;
        this.f$7 = message;
        this.f$8 = i6;
        this.f$9 = longSparseArray;
        this.f$10 = longSparseArray2;
    }

    public final void run() {
        this.f$0.m2264lambda$resetDialogs$69$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}

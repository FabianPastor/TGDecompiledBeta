package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda180 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$messages_Dialogs f$1;
    public final /* synthetic */ LongSparseArray f$10;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ TLRPC$Message f$7;
    public final /* synthetic */ int f$8;
    public final /* synthetic */ LongSparseArray f$9;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda180(MessagesStorage messagesStorage, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i, int i2, int i3, int i4, int i5, TLRPC$Message tLRPC$Message, int i6, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$messages_Dialogs;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = i3;
        this.f$5 = i4;
        this.f$6 = i5;
        this.f$7 = tLRPC$Message;
        this.f$8 = i6;
        this.f$9 = longSparseArray;
        this.f$10 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$resetDialogs$69(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}

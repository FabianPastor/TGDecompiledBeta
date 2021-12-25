package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda35 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC$messages_Dialogs f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ LongSparseArray f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda35(MessagesController messagesController, int i, int i2, int i3, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = tLRPC$messages_Dialogs;
        this.f$5 = longSparseArray;
        this.f$6 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$completeDialogsReset$163(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

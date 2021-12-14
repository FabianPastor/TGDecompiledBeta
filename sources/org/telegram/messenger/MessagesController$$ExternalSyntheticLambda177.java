package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda177 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$messages_Dialogs f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ LongSparseIntArray f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda177(MessagesController messagesController, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, LongSparseIntArray longSparseIntArray) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$messages_Dialogs;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
        this.f$4 = z;
        this.f$5 = longSparseIntArray;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdate$177(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

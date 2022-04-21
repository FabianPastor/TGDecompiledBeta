package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda294 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC.TL_messages_peerDialogs f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ TLRPC.TL_messages_dialogs f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda294(MessagesController messagesController, int i, ArrayList arrayList, boolean z, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TLRPC.TL_messages_dialogs tL_messages_dialogs) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = z;
        this.f$4 = tL_messages_peerDialogs;
        this.f$5 = longSparseArray;
        this.f$6 = tL_messages_dialogs;
    }

    public final void run() {
        this.f$0.m269x141CLASSNAMEf9(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

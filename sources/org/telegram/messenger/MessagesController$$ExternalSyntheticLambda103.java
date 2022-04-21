package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda103 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.updates_ChannelDifference f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC.Chat f$3;
    public final /* synthetic */ LongSparseArray f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ long f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda103(MessagesController messagesController, TLRPC.updates_ChannelDifference updates_channeldifference, long j, TLRPC.Chat chat, LongSparseArray longSparseArray, int i, long j2) {
        this.f$0 = messagesController;
        this.f$1 = updates_channeldifference;
        this.f$2 = j;
        this.f$3 = chat;
        this.f$4 = longSparseArray;
        this.f$5 = i;
        this.f$6 = j2;
    }

    public final void run() {
        this.f$0.m214x81630652(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

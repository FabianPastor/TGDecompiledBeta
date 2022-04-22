package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$updates_ChannelDifference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda193 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_ChannelDifference f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$Chat f$3;
    public final /* synthetic */ LongSparseArray f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ long f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda193(MessagesController messagesController, TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference, long j, TLRPC$Chat tLRPC$Chat, LongSparseArray longSparseArray, int i, long j2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_ChannelDifference;
        this.f$2 = j;
        this.f$3 = tLRPC$Chat;
        this.f$4 = longSparseArray;
        this.f$5 = i;
        this.f$6 = j2;
    }

    public final void run() {
        this.f$0.lambda$getChannelDifference$262(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

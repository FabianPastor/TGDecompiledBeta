package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$updates_ChannelDifference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda106 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$updates_ChannelDifference f$3;
    public final /* synthetic */ TLRPC$Chat f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ long f$7;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda106(MessagesController messagesController, ArrayList arrayList, long j, TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference, TLRPC$Chat tLRPC$Chat, LongSparseArray longSparseArray, int i, long j2) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = j;
        this.f$3 = tLRPC$updates_ChannelDifference;
        this.f$4 = tLRPC$Chat;
        this.f$5 = longSparseArray;
        this.f$6 = i;
        this.f$7 = j2;
    }

    public final void run() {
        this.f$0.lambda$getChannelDifference$256(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}

package org.telegram.messenger;

import android.util.SparseArray;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$e49IKsyhe4ojSofLmQ7VZd8yzMc implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ updates_ChannelDifference f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ Chat f$3;
    private final /* synthetic */ SparseArray f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ long f$6;

    public /* synthetic */ -$$Lambda$MessagesController$e49IKsyhe4ojSofLmQ7VZd8yzMc(MessagesController messagesController, updates_ChannelDifference updates_channeldifference, int i, Chat chat, SparseArray sparseArray, int i2, long j) {
        this.f$0 = messagesController;
        this.f$1 = updates_channeldifference;
        this.f$2 = i;
        this.f$3 = chat;
        this.f$4 = sparseArray;
        this.f$5 = i2;
        this.f$6 = j;
    }

    public final void run() {
        this.f$0.lambda$null$217$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

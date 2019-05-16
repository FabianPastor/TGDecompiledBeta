package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$lCXOkGLPvyvcFRmbZ7785J1HE74 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ updates_ChannelDifference f$3;
    private final /* synthetic */ Chat f$4;
    private final /* synthetic */ SparseArray f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ long f$7;

    public /* synthetic */ -$$Lambda$MessagesController$lCXOkGLPvyvcFRmbZ7785J1HE74(MessagesController messagesController, ArrayList arrayList, int i, updates_ChannelDifference updates_channeldifference, Chat chat, SparseArray sparseArray, int i2, long j) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = i;
        this.f$3 = updates_channeldifference;
        this.f$4 = chat;
        this.f$5 = sparseArray;
        this.f$6 = i2;
        this.f$7 = j;
    }

    public final void run() {
        this.f$0.lambda$null$203$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}

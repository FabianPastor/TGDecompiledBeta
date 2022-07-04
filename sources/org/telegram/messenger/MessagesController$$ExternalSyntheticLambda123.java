package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda123 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.Updates f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda123(MessagesController messagesController, boolean z, TLRPC.Updates updates, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = updates;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.m357x68db04cb(this.f$1, this.f$2, this.f$3);
    }
}

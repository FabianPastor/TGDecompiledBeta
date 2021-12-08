package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda308 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_chatOnlines f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda308(MessagesController messagesController, long j, TLRPC.TL_chatOnlines tL_chatOnlines) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = tL_chatOnlines;
    }

    public final void run() {
        this.f$0.m417x3f0bfab(this.f$1, this.f$2);
    }
}

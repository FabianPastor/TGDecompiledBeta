package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda51 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda51(MessagesController messagesController, TLRPC.TL_error tL_error, long j) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m243lambda$loadFullChat$40$orgtelegrammessengerMessagesController(this.f$1, this.f$2);
    }
}

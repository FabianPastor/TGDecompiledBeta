package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda337 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_messages_chatFull f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda337(MessagesController messagesController, long j, TLRPC.TL_messages_chatFull tL_messages_chatFull, int i) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = tL_messages_chatFull;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.m249lambda$loadFullChat$45$orgtelegrammessengerMessagesController(this.f$1, this.f$2, this.f$3);
    }
}

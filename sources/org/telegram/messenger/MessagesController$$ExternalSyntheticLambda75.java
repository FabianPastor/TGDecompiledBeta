package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda75 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Updates f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda75(MessagesController messagesController, TLRPC.Updates updates) {
        this.f$0 = messagesController;
        this.f$1 = updates;
    }

    public final void run() {
        this.f$0.m161lambda$createChat$197$orgtelegrammessengerMessagesController(this.f$1);
    }
}

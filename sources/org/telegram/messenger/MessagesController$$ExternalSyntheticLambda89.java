package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda89 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda89(MessagesController messagesController, TLRPC.User user) {
        this.f$0 = messagesController;
        this.f$1 = user;
    }

    public final void run() {
        this.f$0.m253lambda$loadFullUser$49$orgtelegrammessengerMessagesController(this.f$1);
    }
}

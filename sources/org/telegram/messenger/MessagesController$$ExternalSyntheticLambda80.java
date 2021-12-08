package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda80 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.UserFull f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda80(MessagesController messagesController, TLRPC.UserFull userFull, TLRPC.User user, int i) {
        this.f$0 = messagesController;
        this.f$1 = userFull;
        this.f$2 = user;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.m245lambda$loadFullUser$42$orgtelegrammessengerMessagesController(this.f$1, this.f$2, this.f$3);
    }
}

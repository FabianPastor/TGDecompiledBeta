package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda47 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Chat f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda47(MessagesController messagesController, TLRPC.Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = chat;
    }

    public final void run() {
        this.f$0.m359lambda$putChat$38$orgtelegrammessengerMessagesController(this.f$1);
    }
}

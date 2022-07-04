package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda84 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_messages_sponsoredMessages f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda84(MessagesController messagesController, TLRPC.TL_messages_sponsoredMessages tL_messages_sponsoredMessages) {
        this.f$0 = messagesController;
        this.f$1 = tL_messages_sponsoredMessages;
    }

    public final void run() {
        this.f$0.m238x32d66cc0(this.f$1);
    }
}

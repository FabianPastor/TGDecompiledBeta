package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda69 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_updateChatUserTyping f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda69(MessagesController messagesController, TLRPC.TL_updateChatUserTyping tL_updateChatUserTyping) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateChatUserTyping;
    }

    public final void run() {
        this.f$0.m323x4f3be228(this.f$1);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda74 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_updateUserTyping f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda74(MessagesController messagesController, TLRPC.TL_updateUserTyping tL_updateUserTyping) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateUserTyping;
    }

    public final void run() {
        this.f$0.m322x69fa7367(this.f$1);
    }
}

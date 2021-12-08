package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda72 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_updatePeerBlocked f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda72(MessagesController messagesController, TLRPC.TL_updatePeerBlocked tL_updatePeerBlocked) {
        this.f$0 = messagesController;
        this.f$1 = tL_updatePeerBlocked;
    }

    public final void run() {
        this.f$0.m325x19bebfaa(this.f$1);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda71 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_updatePeerBlocked f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda71(MessagesController messagesController, TLRPC.TL_updatePeerBlocked tL_updatePeerBlocked) {
        this.f$0 = messagesController;
        this.f$1 = tL_updatePeerBlocked;
    }

    public final void run() {
        this.f$0.m324x347d50e9(this.f$1);
    }
}

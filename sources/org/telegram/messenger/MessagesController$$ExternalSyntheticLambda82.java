package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda82 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_updatePeerBlocked f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda82(MessagesController messagesController, TLRPC.TL_updatePeerBlocked tL_updatePeerBlocked) {
        this.f$0 = messagesController;
        this.f$1 = tL_updatePeerBlocked;
    }

    public final void run() {
        this.f$0.m332xf8cvar_(this.f$1);
    }
}

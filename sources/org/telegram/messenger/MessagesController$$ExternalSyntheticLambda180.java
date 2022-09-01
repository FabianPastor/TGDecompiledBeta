package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updatePeerBlocked;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda180 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_updatePeerBlocked f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda180(MessagesController messagesController, TLRPC$TL_updatePeerBlocked tLRPC$TL_updatePeerBlocked) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_updatePeerBlocked;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$314(this.f$1);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_channels_sendAsPeers;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda152 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_channels_sendAsPeers f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda152(MessagesController messagesController, TLRPC$TL_channels_sendAsPeers tLRPC$TL_channels_sendAsPeers) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_channels_sendAsPeers;
    }

    public final void run() {
        this.f$0.lambda$getSendAsPeers$332(this.f$1);
    }
}

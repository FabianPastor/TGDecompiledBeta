package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda58 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_channels_sendAsPeers f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda58(MessagesController messagesController, TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers) {
        this.f$0 = messagesController;
        this.f$1 = tL_channels_sendAsPeers;
    }

    public final void run() {
        this.f$0.m232xCLASSNAMEcad(this.f$1);
    }
}

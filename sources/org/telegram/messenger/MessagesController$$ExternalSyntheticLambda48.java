package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_channels_sendAsPeers f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ MessagesController.SendAsPeersInfo f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda48(MessagesController messagesController, TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers, long j, MessagesController.SendAsPeersInfo sendAsPeersInfo) {
        this.f$0 = messagesController;
        this.f$1 = tL_channels_sendAsPeers;
        this.f$2 = j;
        this.f$3 = sendAsPeersInfo;
    }

    public final void run() {
        this.f$0.m226x8407b136(this.f$1, this.f$2, this.f$3);
    }
}

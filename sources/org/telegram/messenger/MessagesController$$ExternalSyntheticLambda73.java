package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda73 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_help_promoData f$1;
    public final /* synthetic */ TLRPC.TL_messages_peerDialogs f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda73(MessagesController messagesController, TLRPC.TL_help_promoData tL_help_promoData, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        this.f$0 = messagesController;
        this.f$1 = tL_help_promoData;
        this.f$2 = tL_messages_peerDialogs;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.m142xdCLASSNAMEc(this.f$1, this.f$2, this.f$3);
    }
}

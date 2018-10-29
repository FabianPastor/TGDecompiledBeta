package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;

final /* synthetic */ class MessagesController$$Lambda$221 implements Runnable {
    private final MessagesController arg$1;
    private final TL_help_proxyDataPromo arg$2;
    private final TL_messages_peerDialogs arg$3;
    private final long arg$4;

    MessagesController$$Lambda$221(MessagesController messagesController, TL_help_proxyDataPromo tL_help_proxyDataPromo, TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_help_proxyDataPromo;
        this.arg$3 = tL_messages_peerDialogs;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$null$74$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}

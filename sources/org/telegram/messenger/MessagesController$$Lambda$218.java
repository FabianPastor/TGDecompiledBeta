package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;

final /* synthetic */ class MessagesController$$Lambda$218 implements Runnable {
    private final MessagesController arg$1;
    private final long arg$2;
    private final TL_help_proxyDataPromo arg$3;

    MessagesController$$Lambda$218(MessagesController messagesController, long j, TL_help_proxyDataPromo tL_help_proxyDataPromo) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = tL_help_proxyDataPromo;
    }

    public void run() {
        this.arg$1.lambda$null$77$MessagesController(this.arg$2, this.arg$3);
    }
}

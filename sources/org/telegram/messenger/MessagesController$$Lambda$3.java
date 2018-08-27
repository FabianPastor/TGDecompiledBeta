package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_config;

final /* synthetic */ class MessagesController$$Lambda$3 implements Runnable {
    private final MessagesController arg$1;
    private final TL_config arg$2;

    MessagesController$$Lambda$3(MessagesController messagesController, TL_config tL_config) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_config;
    }

    public void run() {
        this.arg$1.lambda$updateConfig$2$MessagesController(this.arg$2);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateChannel;

final /* synthetic */ class MessagesController$$Lambda$151 implements Runnable {
    private final MessagesController arg$1;
    private final TL_updateChannel arg$2;

    MessagesController$$Lambda$151(MessagesController messagesController, TL_updateChannel tL_updateChannel) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_updateChannel;
    }

    public void run() {
        this.arg$1.lambda$null$227$MessagesController(this.arg$2);
    }
}

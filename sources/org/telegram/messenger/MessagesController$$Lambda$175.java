package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$175 implements Runnable {
    private final MessagesController arg$1;
    private final TL_error arg$2;
    private final int arg$3;

    MessagesController$$Lambda$175(MessagesController messagesController, TL_error tL_error, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_error;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$186$MessagesController(this.arg$2, this.arg$3);
    }
}

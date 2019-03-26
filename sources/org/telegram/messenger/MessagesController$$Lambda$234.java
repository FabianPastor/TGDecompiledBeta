package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_chatOnlines;

final /* synthetic */ class MessagesController$$Lambda$234 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final TL_chatOnlines arg$3;

    MessagesController$$Lambda$234(MessagesController messagesController, int i, TL_chatOnlines tL_chatOnlines) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = tL_chatOnlines;
    }

    public void run() {
        this.arg$1.lambda$null$84$MessagesController(this.arg$2, this.arg$3);
    }
}

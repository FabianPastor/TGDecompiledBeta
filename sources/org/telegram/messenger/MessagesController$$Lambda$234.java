package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesController$$Lambda$234 implements Runnable {
    private final MessagesController arg$1;
    private final long arg$2;
    private final TLObject arg$3;

    MessagesController$$Lambda$234(MessagesController messagesController, long j, TLObject tLObject) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$26$MessagesController(this.arg$2, this.arg$3);
    }
}

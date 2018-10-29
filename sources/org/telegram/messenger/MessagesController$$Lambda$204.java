package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesController$$Lambda$204 implements Runnable {
    private final MessagesController arg$1;
    private final TLObject arg$2;
    private final long arg$3;

    MessagesController$$Lambda$204(MessagesController messagesController, TLObject tLObject, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = tLObject;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$null$109$MessagesController(this.arg$2, this.arg$3);
    }
}

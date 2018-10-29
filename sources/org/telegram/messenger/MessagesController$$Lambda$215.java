package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesController$$Lambda$215 implements Runnable {
    private final MessagesController arg$1;
    private final String arg$2;
    private final TLObject arg$3;
    private final long arg$4;

    MessagesController$$Lambda$215(MessagesController messagesController, String str, TLObject tLObject, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = str;
        this.arg$3 = tLObject;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$null$88$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}

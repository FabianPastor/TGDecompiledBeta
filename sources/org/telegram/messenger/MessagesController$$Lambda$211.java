package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesController$$Lambda$211 implements Runnable {
    private final MessagesController arg$1;
    private final TLObject arg$2;
    private final int arg$3;

    MessagesController$$Lambda$211(MessagesController messagesController, TLObject tLObject, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = tLObject;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$96$MessagesController(this.arg$2, this.arg$3);
    }
}

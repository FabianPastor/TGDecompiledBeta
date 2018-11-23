package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesController$$Lambda$165 implements Runnable {
    private final MessagesController arg$1;
    private final TLObject arg$2;

    MessagesController$$Lambda$165(MessagesController messagesController, TLObject tLObject) {
        this.arg$1 = messagesController;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$199$MessagesController(this.arg$2);
    }
}

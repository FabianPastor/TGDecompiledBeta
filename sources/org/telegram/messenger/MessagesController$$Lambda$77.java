package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class MessagesController$$Lambda$77 implements Runnable {
    private final MessagesController arg$1;
    private final Message arg$2;

    MessagesController$$Lambda$77(MessagesController messagesController, Message message) {
        this.arg$1 = messagesController;
        this.arg$2 = message;
    }

    public void run() {
        this.arg$1.lambda$addToViewsQueue$118$MessagesController(this.arg$2);
    }
}

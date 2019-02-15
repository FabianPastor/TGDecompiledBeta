package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesController$$Lambda$254 implements Runnable {
    private final MessagesController arg$1;
    private final User arg$2;

    MessagesController$$Lambda$254(MessagesController messagesController, User user) {
        this.arg$1 = messagesController;
        this.arg$2 = user;
    }

    public void run() {
        this.arg$1.lambda$null$19$MessagesController(this.arg$2);
    }
}

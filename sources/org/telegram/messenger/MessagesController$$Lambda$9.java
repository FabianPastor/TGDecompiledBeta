package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class MessagesController$$Lambda$9 implements Runnable {
    private final MessagesController arg$1;
    private final Chat arg$2;

    MessagesController$$Lambda$9(MessagesController messagesController, Chat chat) {
        this.arg$1 = messagesController;
        this.arg$2 = chat;
    }

    public void run() {
        this.arg$1.lambda$putChat$9$MessagesController(this.arg$2);
    }
}

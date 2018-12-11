package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatFull;

final /* synthetic */ class MessagesController$$Lambda$190 implements Runnable {
    private final MessagesController arg$1;
    private final ChatFull arg$2;
    private final String arg$3;

    MessagesController$$Lambda$190(MessagesController messagesController, ChatFull chatFull, String str) {
        this.arg$1 = messagesController;
        this.arg$2 = chatFull;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$null$152$MessagesController(this.arg$2, this.arg$3);
    }
}

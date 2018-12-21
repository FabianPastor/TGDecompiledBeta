package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatFull;

final /* synthetic */ class MessagesController$$Lambda$192 implements Runnable {
    private final MessagesController arg$1;
    private final ChatFull arg$2;
    private final String arg$3;

    MessagesController$$Lambda$192(MessagesController messagesController, ChatFull chatFull, String str) {
        this.arg$1 = messagesController;
        this.arg$2 = chatFull;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$null$156$MessagesController(this.arg$2, this.arg$3);
    }
}

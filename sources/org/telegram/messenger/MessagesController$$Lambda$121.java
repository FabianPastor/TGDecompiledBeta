package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class MessagesController$$Lambda$121 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$2;
    private final Chat arg$3;

    MessagesController$$Lambda$121(MessagesController messagesController, boolean z, Chat chat) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
        this.arg$3 = chat;
    }

    public void run() {
        this.arg$1.lambda$startShortPoll$183$MessagesController(this.arg$2, this.arg$3);
    }
}

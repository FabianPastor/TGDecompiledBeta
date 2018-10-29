package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatFull;

final /* synthetic */ class MessagesStorage$$Lambda$42 implements Runnable {
    private final MessagesStorage arg$1;
    private final boolean arg$2;
    private final ChatFull arg$3;

    MessagesStorage$$Lambda$42(MessagesStorage messagesStorage, boolean z, ChatFull chatFull) {
        this.arg$1 = messagesStorage;
        this.arg$2 = z;
        this.arg$3 = chatFull;
    }

    public void run() {
        this.arg$1.lambda$updateChatInfo$62$MessagesStorage(this.arg$2, this.arg$3);
    }
}

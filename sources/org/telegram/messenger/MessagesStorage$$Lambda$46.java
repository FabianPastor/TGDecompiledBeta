package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatFull;

final /* synthetic */ class MessagesStorage$$Lambda$46 implements Runnable {
    private final MessagesStorage arg$1;
    private final ChatFull arg$2;
    private final boolean arg$3;

    MessagesStorage$$Lambda$46(MessagesStorage messagesStorage, ChatFull chatFull, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = chatFull;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$updateChatInfo$69$MessagesStorage(this.arg$2, this.arg$3);
    }
}

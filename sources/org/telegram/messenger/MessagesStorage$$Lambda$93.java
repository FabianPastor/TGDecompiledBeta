package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class MessagesStorage$$Lambda$93 implements Runnable {
    private final MessagesStorage arg$1;
    private final Message arg$2;

    MessagesStorage$$Lambda$93(MessagesStorage messagesStorage, Message message) {
        this.arg$1 = messagesStorage;
        this.arg$2 = message;
    }

    public void run() {
        this.arg$1.lambda$replaceMessageIfExists$122$MessagesStorage(this.arg$2);
    }
}

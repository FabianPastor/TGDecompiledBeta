package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.EncryptedChat;

final /* synthetic */ class MessagesStorage$$Lambda$64 implements Runnable {
    private final MessagesStorage arg$1;
    private final EncryptedChat arg$2;

    MessagesStorage$$Lambda$64(MessagesStorage messagesStorage, EncryptedChat encryptedChat) {
        this.arg$1 = messagesStorage;
        this.arg$2 = encryptedChat;
    }

    public void run() {
        this.arg$1.lambda$updateEncryptedChatLayer$88$MessagesStorage(this.arg$2);
    }
}

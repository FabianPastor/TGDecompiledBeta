package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatParticipants;

final /* synthetic */ class MessagesStorage$$Lambda$37 implements Runnable {
    private final MessagesStorage arg$1;
    private final ChatParticipants arg$2;

    MessagesStorage$$Lambda$37(MessagesStorage messagesStorage, ChatParticipants chatParticipants) {
        this.arg$1 = messagesStorage;
        this.arg$2 = chatParticipants;
    }

    public void run() {
        this.arg$1.lambda$updateChatParticipants$57$MessagesStorage(this.arg$2);
    }
}

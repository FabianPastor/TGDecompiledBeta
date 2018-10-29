package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class MessagesStorage$$Lambda$57 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final IntCallback arg$3;

    MessagesStorage$$Lambda$57(MessagesStorage messagesStorage, long j, IntCallback intCallback) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = intCallback;
    }

    public void run() {
        this.arg$1.lambda$getUnreadMention$80$MessagesStorage(this.arg$2, this.arg$3);
    }
}

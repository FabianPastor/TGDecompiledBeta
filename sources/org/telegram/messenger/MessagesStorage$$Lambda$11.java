package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$11 implements Runnable {
    private final MessagesStorage arg$1;
    private final MessageObject arg$2;

    MessagesStorage$$Lambda$11(MessagesStorage messagesStorage, MessageObject messageObject) {
        this.arg$1 = messagesStorage;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$putPushMessage$22$MessagesStorage(this.arg$2);
    }
}

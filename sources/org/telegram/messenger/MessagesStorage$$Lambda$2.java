package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$2 implements Runnable {
    private final MessagesStorage arg$1;
    private final boolean arg$2;

    MessagesStorage$$Lambda$2(MessagesStorage messagesStorage, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$cleanup$3$MessagesStorage(this.arg$2);
    }
}

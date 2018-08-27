package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$7 implements Runnable {
    private final MessagesStorage arg$1;

    MessagesStorage$$Lambda$7(MessagesStorage messagesStorage) {
        this.arg$1 = messagesStorage;
    }

    public void run() {
        this.arg$1.lambda$loadPendingTasks$15$MessagesStorage();
    }
}

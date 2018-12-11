package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$76 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;

    MessagesStorage$$Lambda$76(MessagesStorage messagesStorage, int i) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$clearDownloadQueue$101$MessagesStorage(this.arg$2);
    }
}

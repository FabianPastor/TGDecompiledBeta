package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$10 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final long arg$3;

    MessagesStorage$$Lambda$10(MessagesStorage messagesStorage, long j, long j2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = j2;
    }

    public void run() {
        this.arg$1.lambda$setDialogFlags$18$MessagesStorage(this.arg$2, this.arg$3);
    }
}

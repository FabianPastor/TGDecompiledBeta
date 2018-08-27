package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$120 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final int arg$3;
    private final long arg$4;

    MessagesStorage$$Lambda$120(MessagesStorage messagesStorage, int i, int i2, long j) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$null$9$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}

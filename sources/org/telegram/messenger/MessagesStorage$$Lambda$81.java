package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$81 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final Integer arg$3;
    private final int arg$4;
    private final int arg$5;
    private final int arg$6;

    MessagesStorage$$Lambda$81(MessagesStorage messagesStorage, long j, Integer num, int i, int i2, int i3) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = num;
        this.arg$4 = i;
        this.arg$5 = i2;
        this.arg$6 = i3;
    }

    public void run() {
        this.arg$1.lambda$updateMessageStateAndId$108$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}

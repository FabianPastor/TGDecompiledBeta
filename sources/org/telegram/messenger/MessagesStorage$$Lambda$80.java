package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$80 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final int arg$3;
    private final int arg$4;

    MessagesStorage$$Lambda$80(MessagesStorage messagesStorage, int i, int i2, int i3) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = i3;
    }

    public void run() {
        this.arg$1.lambda$setMessageSeq$107$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}

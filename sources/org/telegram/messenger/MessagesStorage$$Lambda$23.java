package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$23 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;

    MessagesStorage$$Lambda$23(MessagesStorage messagesStorage, long j, int i, int i2, int i3) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = i3;
    }

    public void run() {
        this.arg$1.lambda$getDialogPhotos$38$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

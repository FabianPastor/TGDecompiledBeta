package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$33 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;
    private final int arg$6;
    private final boolean arg$7;

    MessagesStorage$$Lambda$33(MessagesStorage messagesStorage, int i, int i2, int i3, int i4, int i5, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = i3;
        this.arg$5 = i4;
        this.arg$6 = i5;
        this.arg$7 = z;
    }

    public void run() {
        this.arg$1.lambda$createTaskForMid$51$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}

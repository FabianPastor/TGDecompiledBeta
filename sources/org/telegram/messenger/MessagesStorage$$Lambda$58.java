package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$58 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$10;
    private final int arg$2;
    private final int arg$3;
    private final boolean arg$4;
    private final long arg$5;
    private final int arg$6;
    private final int arg$7;
    private final int arg$8;
    private final int arg$9;

    MessagesStorage$$Lambda$58(MessagesStorage messagesStorage, int i, int i2, boolean z, long j, int i3, int i4, int i5, int i6, int i7) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = z;
        this.arg$5 = j;
        this.arg$6 = i3;
        this.arg$7 = i4;
        this.arg$8 = i5;
        this.arg$9 = i6;
        this.arg$10 = i7;
    }

    public void run() {
        this.arg$1.lambda$getMessages$82$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10);
    }
}

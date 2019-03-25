package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$137 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final long arg$3;
    private final boolean arg$4;
    private final boolean arg$5;
    private final int arg$6;
    private final float arg$7;
    private final boolean arg$8;
    private final long arg$9;

    MessagesStorage$$Lambda$137(MessagesStorage messagesStorage, long j, long j2, boolean z, boolean z2, int i, float f, boolean z3, long j3) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = j2;
        this.arg$4 = z;
        this.arg$5 = z2;
        this.arg$6 = i;
        this.arg$7 = f;
        this.arg$8 = z3;
        this.arg$9 = j3;
    }

    public void run() {
        this.arg$1.lambda$null$16$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}

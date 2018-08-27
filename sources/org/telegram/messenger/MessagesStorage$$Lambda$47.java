package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$47 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final long arg$3;
    private final boolean arg$4;
    private final long arg$5;

    MessagesStorage$$Lambda$47(MessagesStorage messagesStorage, long j, long j2, boolean z, long j3) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = j2;
        this.arg$4 = z;
        this.arg$5 = j3;
    }

    public void run() {
        this.arg$1.lambda$processPendingRead$69$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

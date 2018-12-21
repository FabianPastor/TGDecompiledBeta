package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$77 implements Runnable {
    private final MessagesStorage arg$1;
    private final boolean arg$2;
    private final int arg$3;
    private final long arg$4;

    MessagesStorage$$Lambda$77(MessagesStorage messagesStorage, boolean z, int i, long j) {
        this.arg$1 = messagesStorage;
        this.arg$2 = z;
        this.arg$3 = i;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$removeFromDownloadQueue$103$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}

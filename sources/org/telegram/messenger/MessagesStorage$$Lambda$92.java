package org.telegram.messenger;

final /* synthetic */ class MessagesStorage$$Lambda$92 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final boolean arg$3;

    MessagesStorage$$Lambda$92(MessagesStorage messagesStorage, long j, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$setDialogUnread$120$MessagesStorage(this.arg$2, this.arg$3);
    }
}

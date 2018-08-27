package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$7 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$2;
    private final long arg$3;

    MessagesController$$Lambda$7(MessagesController messagesController, boolean z, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$setLastCreatedDialogId$7$MessagesController(this.arg$2, this.arg$3);
    }
}

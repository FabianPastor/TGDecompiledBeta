package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$216 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final long arg$3;

    MessagesController$$Lambda$216(MessagesController messagesController, int i, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$null$84$MessagesController(this.arg$2, this.arg$3);
    }
}

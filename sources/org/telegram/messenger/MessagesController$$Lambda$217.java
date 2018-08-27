package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$217 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final long arg$3;

    MessagesController$$Lambda$217(MessagesController messagesController, int i, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$null$82$MessagesController(this.arg$2, this.arg$3);
    }
}

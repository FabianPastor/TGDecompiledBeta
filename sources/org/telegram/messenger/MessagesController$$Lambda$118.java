package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$118 implements Runnable {
    private final MessagesController arg$1;
    private final boolean arg$2;
    private final int arg$3;

    MessagesController$$Lambda$118(MessagesController messagesController, boolean z, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = z;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$startShortPoll$178$MessagesController(this.arg$2, this.arg$3);
    }
}

package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$86 implements Runnable {
    private final MessagesController arg$1;
    private final long arg$2;

    MessagesController$$Lambda$86(MessagesController messagesController, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
    }

    public void run() {
        this.arg$1.lambda$markDialogAsReadNow$127$MessagesController(this.arg$2);
    }
}

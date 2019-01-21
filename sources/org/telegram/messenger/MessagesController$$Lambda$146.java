package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$146 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final int arg$3;

    MessagesController$$Lambda$146(MessagesController messagesController, int i, int i2) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = i2;
    }

    public void run() {
        this.arg$1.lambda$processUpdateArray$247$MessagesController(this.arg$2, this.arg$3);
    }
}

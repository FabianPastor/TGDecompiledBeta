package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$195 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final String arg$3;

    MessagesController$$Lambda$195(MessagesController messagesController, int i, String str) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$null$170$MessagesController(this.arg$2, this.arg$3);
    }
}

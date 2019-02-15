package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$22 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;

    MessagesController$$Lambda$22(MessagesController messagesController, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$didAddedNewTask$30$MessagesController(this.arg$2);
    }
}

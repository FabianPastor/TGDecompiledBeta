package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$80 implements Runnable {
    private final MessagesController arg$1;
    private final MessageObject arg$2;

    MessagesController$$Lambda$80(MessagesController messagesController, MessageObject messageObject) {
        this.arg$1 = messagesController;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$addToViewsQueue$123$MessagesController(this.arg$2);
    }
}

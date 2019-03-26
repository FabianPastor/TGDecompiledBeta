package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$86 implements Runnable {
    private final MessagesController arg$1;
    private final MessageObject arg$2;

    MessagesController$$Lambda$86(MessagesController messagesController, MessageObject messageObject) {
        this.arg$1 = messagesController;
        this.arg$2 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$addToViewsQueue$136$MessagesController(this.arg$2);
    }
}

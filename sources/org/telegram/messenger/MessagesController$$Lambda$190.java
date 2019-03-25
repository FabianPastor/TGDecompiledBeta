package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$190 implements Runnable {
    private final MessagesController arg$1;
    private final int arg$2;
    private final boolean arg$3;

    MessagesController$$Lambda$190(MessagesController messagesController, int i, boolean z) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$null$177$MessagesController(this.arg$2, this.arg$3);
    }
}

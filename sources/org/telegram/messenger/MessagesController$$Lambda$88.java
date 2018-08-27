package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$88 implements Runnable {
    private final MessagesController arg$1;
    private final long arg$2;
    private final int arg$3;
    private final int arg$4;
    private final boolean arg$5;

    MessagesController$$Lambda$88(MessagesController messagesController, long j, int i, int i2, boolean z) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = z;
    }

    public void run() {
        this.arg$1.lambda$markDialogAsRead$130$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

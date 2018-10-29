package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$90 implements Runnable {
    private final MessagesController arg$1;
    private final long arg$2;
    private final boolean arg$3;
    private final int arg$4;
    private final int arg$5;

    MessagesController$$Lambda$90(MessagesController messagesController, long j, boolean z, int i, int i2) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = z;
        this.arg$4 = i;
        this.arg$5 = i2;
    }

    public void run() {
        this.arg$1.lambda$markDialogAsRead$133$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

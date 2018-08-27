package org.telegram.messenger;

final /* synthetic */ class MessagesController$$Lambda$89 implements Runnable {
    private final MessagesController arg$1;
    private final long arg$2;
    private final int arg$3;
    private final boolean arg$4;
    private final int arg$5;
    private final int arg$6;

    MessagesController$$Lambda$89(MessagesController messagesController, long j, int i, boolean z, int i2, int i3) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = z;
        this.arg$5 = i2;
        this.arg$6 = i3;
    }

    public void run() {
        this.arg$1.lambda$markDialogAsRead$132$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}

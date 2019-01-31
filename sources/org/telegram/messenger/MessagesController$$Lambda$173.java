package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.updates_Difference;

final /* synthetic */ class MessagesController$$Lambda$173 implements Runnable {
    private final MessagesController arg$1;
    private final updates_Difference arg$2;
    private final int arg$3;
    private final int arg$4;

    MessagesController$$Lambda$173(MessagesController messagesController, updates_Difference updates_difference, int i, int i2) {
        this.arg$1 = messagesController;
        this.arg$2 = updates_difference;
        this.arg$3 = i;
        this.arg$4 = i2;
    }

    public void run() {
        this.arg$1.lambda$null$203$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}

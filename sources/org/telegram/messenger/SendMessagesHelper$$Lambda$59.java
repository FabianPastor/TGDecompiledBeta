package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SendMessagesHelper$$Lambda$59 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Message arg$2;
    private final long arg$3;
    private final int arg$4;
    private final Message arg$5;

    SendMessagesHelper$$Lambda$59(SendMessagesHelper sendMessagesHelper, Message message, long j, int i, Message message2) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = message;
        this.arg$3 = j;
        this.arg$4 = i;
        this.arg$5 = message2;
    }

    public void run() {
        this.arg$1.lambda$null$5$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

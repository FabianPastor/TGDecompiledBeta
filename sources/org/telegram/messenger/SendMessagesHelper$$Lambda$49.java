package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SendMessagesHelper$$Lambda$49 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Message arg$2;
    private final int arg$3;
    private final long arg$4;

    SendMessagesHelper$$Lambda$49(SendMessagesHelper sendMessagesHelper, Message message, int i, long j) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = message;
        this.arg$3 = i;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$null$24$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4);
    }
}

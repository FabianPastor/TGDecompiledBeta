package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SendMessagesHelper$$Lambda$33 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Message arg$2;
    private final int arg$3;

    SendMessagesHelper$$Lambda$33(SendMessagesHelper sendMessagesHelper, Message message, int i) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = message;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$40$SendMessagesHelper(this.arg$2, this.arg$3);
    }
}

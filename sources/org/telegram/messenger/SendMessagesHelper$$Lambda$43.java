package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class SendMessagesHelper$$Lambda$43 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Updates arg$2;
    private final Message arg$3;

    SendMessagesHelper$$Lambda$43(SendMessagesHelper sendMessagesHelper, Updates updates, Message message) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = updates;
        this.arg$3 = message;
    }

    public void run() {
        this.arg$1.lambda$null$31$SendMessagesHelper(this.arg$2, this.arg$3);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class SendMessagesHelper$$Lambda$39 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final Updates arg$2;

    SendMessagesHelper$$Lambda$39(SendMessagesHelper sendMessagesHelper, Updates updates) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = updates;
    }

    public void run() {
        this.arg$1.lambda$null$35$SendMessagesHelper(this.arg$2);
    }
}

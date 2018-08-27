package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;

final /* synthetic */ class SendMessagesHelper$$Lambda$36 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_updateShortSentMessage arg$2;

    SendMessagesHelper$$Lambda$36(SendMessagesHelper sendMessagesHelper, TL_updateShortSentMessage tL_updateShortSentMessage) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_updateShortSentMessage;
    }

    public void run() {
        this.arg$1.lambda$null$32$SendMessagesHelper(this.arg$2);
    }
}

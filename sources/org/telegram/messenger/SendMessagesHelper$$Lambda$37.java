package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateNewMessage;

final /* synthetic */ class SendMessagesHelper$$Lambda$37 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_updateNewMessage arg$2;

    SendMessagesHelper$$Lambda$37(SendMessagesHelper sendMessagesHelper, TL_updateNewMessage tL_updateNewMessage) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_updateNewMessage;
    }

    public void run() {
        this.arg$1.lambda$null$33$SendMessagesHelper(this.arg$2);
    }
}

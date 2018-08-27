package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;

final /* synthetic */ class SendMessagesHelper$$Lambda$46 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_updateNewChannelMessage arg$2;

    SendMessagesHelper$$Lambda$46(SendMessagesHelper sendMessagesHelper, TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_updateNewChannelMessage;
    }

    public void run() {
        this.arg$1.lambda$null$23$SendMessagesHelper(this.arg$2);
    }
}

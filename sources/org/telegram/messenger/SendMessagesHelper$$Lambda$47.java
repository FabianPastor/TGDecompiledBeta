package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;

final /* synthetic */ class SendMessagesHelper$$Lambda$47 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_updateNewChannelMessage arg$2;

    SendMessagesHelper$$Lambda$47(SendMessagesHelper sendMessagesHelper, TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_updateNewChannelMessage;
    }

    public void run() {
        this.arg$1.lambda$null$25$SendMessagesHelper(this.arg$2);
    }
}

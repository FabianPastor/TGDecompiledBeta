package org.telegram.messenger;

import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class SendMessagesHelper$$Lambda$13 implements QuickAckDelegate {
    private final SendMessagesHelper arg$1;
    private final Message arg$2;

    SendMessagesHelper$$Lambda$13(SendMessagesHelper sendMessagesHelper, Message message) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = message;
    }

    public void run() {
        this.arg$1.lambda$performSendMessageRequest$42$SendMessagesHelper(this.arg$2);
    }
}

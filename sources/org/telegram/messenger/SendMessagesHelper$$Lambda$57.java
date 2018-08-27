package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;

final /* synthetic */ class SendMessagesHelper$$Lambda$57 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_error arg$2;
    private final TL_messages_forwardMessages arg$3;

    SendMessagesHelper$$Lambda$57(SendMessagesHelper sendMessagesHelper, TL_error tL_error, TL_messages_forwardMessages tL_messages_forwardMessages) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_error;
        this.arg$3 = tL_messages_forwardMessages;
    }

    public void run() {
        this.arg$1.lambda$null$7$SendMessagesHelper(this.arg$2, this.arg$3);
    }
}

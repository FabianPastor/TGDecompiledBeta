package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$35 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_error arg$2;
    private final Message arg$3;
    private final TLObject arg$4;
    private final TLObject arg$5;
    private final MessageObject arg$6;
    private final String arg$7;

    SendMessagesHelper$$Lambda$35(SendMessagesHelper sendMessagesHelper, TL_error tL_error, Message message, TLObject tLObject, TLObject tLObject2, MessageObject messageObject, String str) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_error;
        this.arg$3 = message;
        this.arg$4 = tLObject;
        this.arg$5 = tLObject2;
        this.arg$6 = messageObject;
        this.arg$7 = str;
    }

    public void run() {
        this.arg$1.lambda$null$38$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}

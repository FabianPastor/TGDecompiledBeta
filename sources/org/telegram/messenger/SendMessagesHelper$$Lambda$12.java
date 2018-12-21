package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$12 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final TLObject arg$2;
    private final Object arg$3;
    private final MessageObject arg$4;
    private final String arg$5;
    private final DelayedMessage arg$6;
    private final boolean arg$7;
    private final DelayedMessage arg$8;
    private final Message arg$9;

    SendMessagesHelper$$Lambda$12(SendMessagesHelper sendMessagesHelper, TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Message message) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tLObject;
        this.arg$3 = obj;
        this.arg$4 = messageObject;
        this.arg$5 = str;
        this.arg$6 = delayedMessage;
        this.arg$7 = z;
        this.arg$8 = delayedMessage2;
        this.arg$9 = message;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$performSendMessageRequest$41$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, tLObject, tL_error);
    }
}

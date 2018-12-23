package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$6 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final MessageObject arg$2;
    private final String arg$3;
    private final Runnable arg$4;

    SendMessagesHelper$$Lambda$6(SendMessagesHelper sendMessagesHelper, MessageObject messageObject, String str, Runnable runnable) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = messageObject;
        this.arg$3 = str;
        this.arg$4 = runnable;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$sendVote$15$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}

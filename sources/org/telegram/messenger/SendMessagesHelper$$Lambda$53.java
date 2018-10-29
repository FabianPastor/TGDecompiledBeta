package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$53 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final String arg$2;

    SendMessagesHelper$$Lambda$53(SendMessagesHelper sendMessagesHelper, String str) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$13$SendMessagesHelper(this.arg$2, tLObject, tL_error);
    }
}

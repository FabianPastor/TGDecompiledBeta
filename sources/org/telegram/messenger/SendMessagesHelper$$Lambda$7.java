package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$7 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final long arg$2;

    SendMessagesHelper$$Lambda$7(SendMessagesHelper sendMessagesHelper, long j) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$sendGame$17$SendMessagesHelper(this.arg$2, tLObject, tL_error);
    }
}

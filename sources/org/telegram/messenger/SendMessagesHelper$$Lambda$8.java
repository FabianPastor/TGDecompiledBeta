package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$8 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final InputMedia arg$2;
    private final DelayedMessage arg$3;

    SendMessagesHelper$$Lambda$8(SendMessagesHelper sendMessagesHelper, InputMedia inputMedia, DelayedMessage delayedMessage) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = inputMedia;
        this.arg$3 = delayedMessage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$uploadMultiMedia$19$SendMessagesHelper(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}

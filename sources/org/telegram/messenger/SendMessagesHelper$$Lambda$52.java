package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputMedia;

final /* synthetic */ class SendMessagesHelper$$Lambda$52 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TLObject arg$2;
    private final InputMedia arg$3;
    private final DelayedMessage arg$4;

    SendMessagesHelper$$Lambda$52(SendMessagesHelper sendMessagesHelper, TLObject tLObject, InputMedia inputMedia, DelayedMessage delayedMessage) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tLObject;
        this.arg$3 = inputMedia;
        this.arg$4 = delayedMessage;
    }

    public void run() {
        this.arg$1.lambda$null$19$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4);
    }
}

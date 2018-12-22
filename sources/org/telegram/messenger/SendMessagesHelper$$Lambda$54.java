package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SendMessagesHelper$$Lambda$54 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final String arg$2;
    private final TL_error arg$3;
    private final MessageObject arg$4;
    private final TLObject arg$5;
    private final Runnable arg$6;

    SendMessagesHelper$$Lambda$54(SendMessagesHelper sendMessagesHelper, String str, TL_error tL_error, MessageObject messageObject, TLObject tLObject, Runnable runnable) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = str;
        this.arg$3 = tL_error;
        this.arg$4 = messageObject;
        this.arg$5 = tLObject;
        this.arg$6 = runnable;
    }

    public void run() {
        this.arg$1.lambda$null$15$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}

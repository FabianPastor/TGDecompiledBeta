package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda61 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.TL_updateNewMessage f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda61(SendMessagesHelper sendMessagesHelper, TLRPC.TL_updateNewMessage tL_updateNewMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_updateNewMessage;
    }

    public final void run() {
        this.f$0.m446x669fcd56(this.f$1);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda58 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.TL_updateNewChannelMessage f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda58(SendMessagesHelper sendMessagesHelper, TLRPC.TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_updateNewChannelMessage;
    }

    public final void run() {
        this.f$0.m435xb1538749(this.f$1);
    }
}

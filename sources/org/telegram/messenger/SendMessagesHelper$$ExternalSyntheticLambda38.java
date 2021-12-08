package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda38 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda38(SendMessagesHelper sendMessagesHelper, TLRPC.Message message, int i) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m443xa9bd1326(this.f$1, this.f$2);
    }
}

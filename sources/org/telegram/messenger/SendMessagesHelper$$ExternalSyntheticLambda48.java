package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda48(SendMessagesHelper sendMessagesHelper, TLRPC.Message message, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.m449x68ae47af(this.f$1, this.f$2);
    }
}

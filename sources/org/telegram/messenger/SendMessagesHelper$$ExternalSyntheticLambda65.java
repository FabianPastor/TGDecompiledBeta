package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda65 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Updates f$1;
    public final /* synthetic */ TLRPC.Message f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda65(SendMessagesHelper sendMessagesHelper, TLRPC.Updates updates, TLRPC.Message message, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = updates;
        this.f$2 = message;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m442x4defb670(this.f$1, this.f$2, this.f$3);
    }
}

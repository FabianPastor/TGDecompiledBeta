package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda40(SendMessagesHelper sendMessagesHelper, TLRPC.Message message, int i, int i2, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.m439x4659424d(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda42(SendMessagesHelper sendMessagesHelper, TLRPC.Message message, int i, long j, int i2, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
        this.f$5 = z;
    }

    public final void run() {
        this.f$0.m467x3122aad8(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

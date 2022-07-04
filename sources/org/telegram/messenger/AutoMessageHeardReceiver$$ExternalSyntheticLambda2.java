package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AutoMessageHeardReceiver$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ AutoMessageHeardReceiver$$ExternalSyntheticLambda2(AccountInstance accountInstance, TLRPC.Chat chat, int i, long j, int i2) {
        this.f$0 = accountInstance;
        this.f$1 = chat;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
    }

    public final void run() {
        AutoMessageHeardReceiver.lambda$onReceive$2(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

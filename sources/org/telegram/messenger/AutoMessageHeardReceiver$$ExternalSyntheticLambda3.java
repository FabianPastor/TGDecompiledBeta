package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AutoMessageHeardReceiver$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ AutoMessageHeardReceiver$$ExternalSyntheticLambda3(AccountInstance accountInstance, TLRPC.User user, int i, long j, int i2) {
        this.f$0 = accountInstance;
        this.f$1 = user;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
    }

    public final void run() {
        AutoMessageHeardReceiver.lambda$onReceive$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

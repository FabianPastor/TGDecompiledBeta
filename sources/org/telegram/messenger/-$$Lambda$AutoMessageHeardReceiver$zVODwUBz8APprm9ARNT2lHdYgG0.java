package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AutoMessageHeardReceiver$zVODwUBz8APprm9ARNT2lHdYgG0 implements Runnable {
    private final /* synthetic */ AccountInstance f$0;
    private final /* synthetic */ User f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$AutoMessageHeardReceiver$zVODwUBz8APprm9ARNT2lHdYgG0(AccountInstance accountInstance, User user, int i, long j, int i2) {
        this.f$0 = accountInstance;
        this.f$1 = user;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
    }

    public final void run() {
        AutoMessageHeardReceiver.lambda$null$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

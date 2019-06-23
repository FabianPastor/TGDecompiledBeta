package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AutoMessageHeardReceiver$5gHjgkav0Vsi6hdGsimdfmozlgg implements Runnable {
    private final /* synthetic */ AccountInstance f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$AutoMessageHeardReceiver$5gHjgkav0Vsi6hdGsimdfmozlgg(AccountInstance accountInstance, Chat chat, int i, long j, int i2) {
        this.f$0 = accountInstance;
        this.f$1 = chat;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
    }

    public final void run() {
        AutoMessageHeardReceiver.lambda$null$2(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

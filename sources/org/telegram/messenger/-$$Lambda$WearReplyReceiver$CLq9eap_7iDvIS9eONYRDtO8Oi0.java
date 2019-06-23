package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearReplyReceiver$CLq9eap_7iDvIS9eONYRDtO8Oi0 implements Runnable {
    private final /* synthetic */ WearReplyReceiver f$0;
    private final /* synthetic */ AccountInstance f$1;
    private final /* synthetic */ User f$2;
    private final /* synthetic */ CharSequence f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$WearReplyReceiver$CLq9eap_7iDvIS9eONYRDtO8Oi0(WearReplyReceiver wearReplyReceiver, AccountInstance accountInstance, User user, CharSequence charSequence, long j, int i) {
        this.f$0 = wearReplyReceiver;
        this.f$1 = accountInstance;
        this.f$2 = user;
        this.f$3 = charSequence;
        this.f$4 = j;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.lambda$null$0$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

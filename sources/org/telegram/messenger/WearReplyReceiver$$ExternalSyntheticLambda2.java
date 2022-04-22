package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class WearReplyReceiver$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ WearReplyReceiver f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ TLRPC$Chat f$2;
    public final /* synthetic */ CharSequence f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ WearReplyReceiver$$ExternalSyntheticLambda2(WearReplyReceiver wearReplyReceiver, AccountInstance accountInstance, TLRPC$Chat tLRPC$Chat, CharSequence charSequence, long j, int i) {
        this.f$0 = wearReplyReceiver;
        this.f$1 = accountInstance;
        this.f$2 = tLRPC$Chat;
        this.f$3 = charSequence;
        this.f$4 = j;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.lambda$onReceive$2(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

package org.telegram.messenger;

public final /* synthetic */ class WearReplyReceiver$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ WearReplyReceiver f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ CharSequence f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ WearReplyReceiver$$ExternalSyntheticLambda0(WearReplyReceiver wearReplyReceiver, AccountInstance accountInstance, long j, CharSequence charSequence, int i) {
        this.f$0 = wearReplyReceiver;
        this.f$1 = accountInstance;
        this.f$2 = j;
        this.f$3 = charSequence;
        this.f$4 = i;
    }

    public final void run() {
        this.f$0.lambda$onReceive$1(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
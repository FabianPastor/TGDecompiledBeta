package org.telegram.messenger;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda2(AccountInstance accountInstance, long j, int i) {
        this.f$0 = accountInstance;
        this.f$1 = j;
        this.f$2 = i;
    }

    public final void run() {
        SendMessagesHelper.lambda$finishGroup$76(this.f$0, this.f$1, this.f$2);
    }
}

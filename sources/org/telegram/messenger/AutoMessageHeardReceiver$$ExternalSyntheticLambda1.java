package org.telegram.messenger;

public final /* synthetic */ class AutoMessageHeardReceiver$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ AutoMessageHeardReceiver$$ExternalSyntheticLambda1(AccountInstance accountInstance, long j, int i, int i2) {
        this.f$0 = accountInstance;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(new AutoMessageHeardReceiver$$ExternalSyntheticLambda2(this.f$0, this.f$0.getMessagesStorage().getChatSync(-this.f$1), this.f$2, this.f$1, this.f$3));
    }
}

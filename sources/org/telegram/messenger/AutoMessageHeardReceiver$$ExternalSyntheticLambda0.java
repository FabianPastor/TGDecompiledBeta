package org.telegram.messenger;

public final /* synthetic */ class AutoMessageHeardReceiver$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ AutoMessageHeardReceiver$$ExternalSyntheticLambda0(AccountInstance accountInstance, int i, int i2, long j, int i3) {
        this.f$0 = accountInstance;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = j;
        this.f$4 = i3;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(new AutoMessageHeardReceiver$$ExternalSyntheticLambda2(this.f$0, this.f$0.getMessagesStorage().getChatSync(-this.f$1), this.f$2, this.f$3, this.f$4));
    }
}

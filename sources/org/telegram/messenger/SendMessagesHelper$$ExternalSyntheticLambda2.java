package org.telegram.messenger;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ AccountInstance f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda2(int i, AccountInstance accountInstance) {
        this.f$0 = i;
        this.f$1 = accountInstance;
    }

    public final void run() {
        SendMessagesHelper.lambda$handleError$78(this.f$0, this.f$1);
    }
}

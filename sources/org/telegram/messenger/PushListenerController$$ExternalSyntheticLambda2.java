package org.telegram.messenger;

public final /* synthetic */ class PushListenerController$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ PushListenerController$$ExternalSyntheticLambda2(int i, int i2, String str) {
        this.f$0 = i;
        this.f$1 = i2;
        this.f$2 = str;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).registerForPush(this.f$1, this.f$2);
    }
}

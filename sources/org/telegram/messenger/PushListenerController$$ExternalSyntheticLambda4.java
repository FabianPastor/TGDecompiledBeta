package org.telegram.messenger;

public final /* synthetic */ class PushListenerController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ PushListenerController$$ExternalSyntheticLambda4(String str, int i) {
        this.f$0 = str;
        this.f$1 = i;
    }

    public final void run() {
        PushListenerController.lambda$sendRegistrationToServer$3(this.f$0, this.f$1);
    }
}

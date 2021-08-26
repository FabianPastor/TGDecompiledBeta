package org.telegram.messenger;

public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda2(int i, String str) {
        this.f$0 = i;
        this.f$1 = str;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).registerForPush(this.f$1);
    }
}

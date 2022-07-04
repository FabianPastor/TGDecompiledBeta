package org.telegram.messenger;

public final /* synthetic */ class FingerprintController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ FingerprintController$$ExternalSyntheticLambda0(boolean z) {
        this.f$0 = z;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didGenerateFingerprintKeyPair, Boolean.valueOf(this.f$0));
    }
}

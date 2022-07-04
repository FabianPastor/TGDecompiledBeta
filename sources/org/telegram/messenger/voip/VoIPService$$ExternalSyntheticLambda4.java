package org.telegram.messenger.voip;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ NativeInstance f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda4(NativeInstance nativeInstance) {
        this.f$0 = nativeInstance;
    }

    public final void run() {
        this.f$0.stopGroup();
    }
}

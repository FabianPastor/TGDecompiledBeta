package org.telegram.messenger.voip;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda40(VoIPService voIPService, int i) {
        this.f$0 = voIPService;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onConnectionStateChanged$83(this.f$1);
    }
}

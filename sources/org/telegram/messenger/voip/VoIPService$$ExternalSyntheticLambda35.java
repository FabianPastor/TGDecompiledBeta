package org.telegram.messenger.voip;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda35 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda35(VoIPService voIPService, int i) {
        this.f$0 = voIPService;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$updateConnectionState$49(this.f$1);
    }
}

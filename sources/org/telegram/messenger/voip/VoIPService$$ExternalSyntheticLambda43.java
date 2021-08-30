package org.telegram.messenger.voip;

import android.media.AudioManager;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda43 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ AudioManager f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda43(VoIPService voIPService, AudioManager audioManager) {
        this.f$0 = voIPService;
        this.f$1 = audioManager;
    }

    public final void run() {
        this.f$0.lambda$configureDeviceForCall$76(this.f$1);
    }
}

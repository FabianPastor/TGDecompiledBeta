package org.telegram.messenger.voip;

import android.media.AudioManager;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda44 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ AudioManager f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda44(VoIPService voIPService, AudioManager audioManager) {
        this.f$0 = voIPService;
        this.f$1 = audioManager;
    }

    public final void run() {
        this.f$0.lambda$configureDeviceForCall$75(this.f$1);
    }
}

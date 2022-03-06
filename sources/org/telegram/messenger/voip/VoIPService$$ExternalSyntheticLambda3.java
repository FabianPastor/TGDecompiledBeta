package org.telegram.messenger.voip;

import android.media.AudioManager;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ AudioManager f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda3(AudioManager audioManager) {
        this.f$0 = audioManager;
    }

    public final void run() {
        VoIPService.lambda$onDestroy$65(this.f$0);
    }
}

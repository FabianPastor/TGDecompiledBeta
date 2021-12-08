package org.telegram.messenger.voip;

import android.media.AudioManager;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ AudioManager f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda22(AudioManager audioManager) {
        this.f$0 = audioManager;
    }

    public final void run() {
        VoIPService.lambda$onDestroy$63(this.f$0);
    }
}

package org.telegram.messenger.voip;

import android.media.AudioManager;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ AudioManager f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda33(AudioManager audioManager) {
        this.f$0 = audioManager;
    }

    public final void run() {
        VoIPService.lambda$updateBluetoothHeadsetState$80(this.f$0);
    }
}

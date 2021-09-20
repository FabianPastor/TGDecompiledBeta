package org.telegram.messenger.voip;

import android.media.MediaPlayer;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda1 implements MediaPlayer.OnPreparedListener {
    public final /* synthetic */ VoIPService f$0;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda1(VoIPService voIPService) {
        this.f$0 = voIPService;
    }

    public final void onPrepared(MediaPlayer mediaPlayer) {
        this.f$0.lambda$startRingtoneAndVibration$61(mediaPlayer);
    }
}

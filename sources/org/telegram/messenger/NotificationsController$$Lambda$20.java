package org.telegram.messenger;

import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

final /* synthetic */ class NotificationsController$$Lambda$20 implements OnLoadCompleteListener {
    static final OnLoadCompleteListener $instance = new NotificationsController$$Lambda$20();

    private NotificationsController$$Lambda$20() {
    }

    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$null$24$NotificationsController(soundPool, i, i2);
    }
}

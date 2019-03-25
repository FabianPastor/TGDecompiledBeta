package org.telegram.messenger;

import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

final /* synthetic */ class NotificationsController$$Lambda$24 implements OnLoadCompleteListener {
    static final OnLoadCompleteListener $instance = new NotificationsController$$Lambda$24();

    private NotificationsController$$Lambda$24() {
    }

    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$null$26$NotificationsController(soundPool, i, i2);
    }
}

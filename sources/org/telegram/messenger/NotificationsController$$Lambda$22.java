package org.telegram.messenger;

import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

final /* synthetic */ class NotificationsController$$Lambda$22 implements OnLoadCompleteListener {
    static final OnLoadCompleteListener $instance = new NotificationsController$$Lambda$22();

    private NotificationsController$$Lambda$22() {
    }

    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$null$32$NotificationsController(soundPool, i, i2);
    }
}

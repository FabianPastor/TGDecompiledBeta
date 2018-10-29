package org.telegram.messenger;

import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

final /* synthetic */ class NotificationsController$$Lambda$18 implements OnLoadCompleteListener {
    static final OnLoadCompleteListener $instance = new NotificationsController$$Lambda$18();

    private NotificationsController$$Lambda$18() {
    }

    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$null$29$NotificationsController(soundPool, i, i2);
    }
}

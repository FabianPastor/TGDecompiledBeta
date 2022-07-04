package org.telegram.messenger;

import android.media.SoundPool;

public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda22 implements SoundPool.OnLoadCompleteListener {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda22 INSTANCE = new NotificationsController$$ExternalSyntheticLambda22();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda22() {
    }

    public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$playInChatSound$28(soundPool, i, i2);
    }
}

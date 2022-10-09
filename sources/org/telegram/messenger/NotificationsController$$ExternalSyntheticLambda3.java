package org.telegram.messenger;

import android.media.SoundPool;
/* loaded from: classes.dex */
public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda3 implements SoundPool.OnLoadCompleteListener {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda3 INSTANCE = new NotificationsController$$ExternalSyntheticLambda3();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda3() {
    }

    @Override // android.media.SoundPool.OnLoadCompleteListener
    public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$playInChatSound$28(soundPool, i, i2);
    }
}

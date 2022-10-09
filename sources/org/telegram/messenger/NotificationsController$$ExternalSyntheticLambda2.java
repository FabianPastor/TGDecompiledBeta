package org.telegram.messenger;

import android.media.SoundPool;
/* loaded from: classes.dex */
public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda2 implements SoundPool.OnLoadCompleteListener {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda2 INSTANCE = new NotificationsController$$ExternalSyntheticLambda2();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda2() {
    }

    @Override // android.media.SoundPool.OnLoadCompleteListener
    public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$playOutChatSound$37(soundPool, i, i2);
    }
}

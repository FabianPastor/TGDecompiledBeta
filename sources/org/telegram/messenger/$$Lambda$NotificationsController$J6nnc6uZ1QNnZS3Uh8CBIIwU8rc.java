package org.telegram.messenger;

import android.media.SoundPool;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$J6nnc6uZ1QNnZS3Uh8CBIIwU8rc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$J6nnc6uZ1QNnZS3Uh8CBIIwU8rc implements SoundPool.OnLoadCompleteListener {
    public static final /* synthetic */ $$Lambda$NotificationsController$J6nnc6uZ1QNnZS3Uh8CBIIwU8rc INSTANCE = new $$Lambda$NotificationsController$J6nnc6uZ1QNnZS3Uh8CBIIwU8rc();

    private /* synthetic */ $$Lambda$NotificationsController$J6nnc6uZ1QNnZS3Uh8CBIIwU8rc() {
    }

    public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$playInChatSound$28(soundPool, i, i2);
    }
}

package org.telegram.messenger;

import android.media.SoundPool;

/* renamed from: org.telegram.messenger.-$$Lambda$NotificationsController$3kQAiZ9Bh16atkj_BNBB6fIwBz0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$3kQAiZ9Bh16atkj_BNBB6fIwBz0 implements SoundPool.OnLoadCompleteListener {
    public static final /* synthetic */ $$Lambda$NotificationsController$3kQAiZ9Bh16atkj_BNBB6fIwBz0 INSTANCE = new $$Lambda$NotificationsController$3kQAiZ9Bh16atkj_BNBB6fIwBz0();

    private /* synthetic */ $$Lambda$NotificationsController$3kQAiZ9Bh16atkj_BNBB6fIwBz0() {
    }

    public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$playOutChatSound$37(soundPool, i, i2);
    }
}

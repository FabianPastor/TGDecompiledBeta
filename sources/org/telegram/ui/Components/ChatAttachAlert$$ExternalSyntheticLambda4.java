package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda4 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda4(ChatAttachAlert chatAttachAlert, Runnable runnable) {
        this.f$0 = chatAttachAlert;
        this.f$1 = runnable;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.m3779xdd86b746(this.f$1, dynamicAnimation, z, f, f2);
    }
}

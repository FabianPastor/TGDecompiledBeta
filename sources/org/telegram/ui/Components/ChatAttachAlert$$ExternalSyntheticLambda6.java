package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda6 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda6(ChatAttachAlert chatAttachAlert, Runnable runnable) {
        this.f$0 = chatAttachAlert;
        this.f$1 = runnable;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.m734xa07320a5(this.f$1, dynamicAnimation, z, f, f2);
    }
}

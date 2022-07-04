package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda16 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda16(ChatAttachAlert chatAttachAlert, boolean z, Runnable runnable) {
        this.f$0 = chatAttachAlert;
        this.f$1 = z;
        this.f$2 = runnable;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$showLayout$22(this.f$1, this.f$2, dynamicAnimation, z, f, f2);
    }
}

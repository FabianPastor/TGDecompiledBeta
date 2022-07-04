package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda8 implements DynamicAnimation.OnAnimationUpdateListener {
    public final /* synthetic */ ChatAttachAlert f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda8(ChatAttachAlert chatAttachAlert, float f, float f2, boolean z) {
        this.f$0 = chatAttachAlert;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = z;
    }

    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.f$0.m742lambda$showLayout$21$orgtelegramuiComponentsChatAttachAlert(this.f$1, this.f$2, this.f$3, dynamicAnimation, f, f2);
    }
}

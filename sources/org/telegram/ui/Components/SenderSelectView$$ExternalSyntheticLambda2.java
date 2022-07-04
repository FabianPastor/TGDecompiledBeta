package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class SenderSelectView$$ExternalSyntheticLambda2 implements DynamicAnimation.OnAnimationUpdateListener {
    public final /* synthetic */ SenderSelectView f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;

    public /* synthetic */ SenderSelectView$$ExternalSyntheticLambda2(SenderSelectView senderSelectView, boolean z, float f, float f2) {
        this.f$0 = senderSelectView;
        this.f$1 = z;
        this.f$2 = f;
        this.f$3 = f2;
    }

    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.f$0.m1345lambda$setProgress$2$orgtelegramuiComponentsSenderSelectView(this.f$1, this.f$2, this.f$3, dynamicAnimation, f, f2);
    }
}

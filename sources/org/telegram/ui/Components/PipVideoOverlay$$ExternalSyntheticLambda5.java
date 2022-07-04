package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class PipVideoOverlay$$ExternalSyntheticLambda5 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ PipVideoOverlay f$0;

    public /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda5(PipVideoOverlay pipVideoOverlay) {
        this.f$0 = pipVideoOverlay;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$showInternal$8(dynamicAnimation, z, f, f2);
    }
}

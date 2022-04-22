package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class SenderSelectPopup$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ SenderSelectPopup f$0;

    public /* synthetic */ SenderSelectPopup$$ExternalSyntheticLambda0(SenderSelectPopup senderSelectPopup) {
        this.f$0 = senderSelectPopup;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$startDismissAnimation$7(dynamicAnimation, z, f, f2);
    }
}

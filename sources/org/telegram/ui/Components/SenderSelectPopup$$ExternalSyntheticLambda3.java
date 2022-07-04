package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;

public final /* synthetic */ class SenderSelectPopup$$ExternalSyntheticLambda3 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ SenderSelectPopup f$0;
    public final /* synthetic */ SpringAnimation f$1;

    public /* synthetic */ SenderSelectPopup$$ExternalSyntheticLambda3(SenderSelectPopup senderSelectPopup, SpringAnimation springAnimation) {
        this.f$0 = senderSelectPopup;
        this.f$1 = springAnimation;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$startDismissAnimation$8(this.f$1, dynamicAnimation, z, f, f2);
    }
}

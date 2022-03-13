package org.telegram.messenger;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda4 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda4(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        AndroidUtilities.lambda$shakeViewSpring$4(this.f$0, dynamicAnimation, z, f, f2);
    }
}

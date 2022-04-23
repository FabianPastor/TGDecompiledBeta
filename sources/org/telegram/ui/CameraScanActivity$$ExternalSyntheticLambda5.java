package org.telegram.ui;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class CameraScanActivity$$ExternalSyntheticLambda5 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ CameraScanActivity f$0;

    public /* synthetic */ CameraScanActivity$$ExternalSyntheticLambda5(CameraScanActivity cameraScanActivity) {
        this.f$0 = cameraScanActivity;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$initCameraView$8(dynamicAnimation, z, f, f2);
    }
}

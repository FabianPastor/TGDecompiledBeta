package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.Window;

public final /* synthetic */ class CameraScanActivity$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ Window f$0;

    public /* synthetic */ CameraScanActivity$$ExternalSyntheticLambda0(Window window) {
        this.f$0 = window;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.setNavigationBarColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}

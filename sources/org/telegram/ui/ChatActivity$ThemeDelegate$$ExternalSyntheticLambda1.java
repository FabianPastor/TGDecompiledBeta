package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.MotionBackgroundDrawable;

public final /* synthetic */ class ChatActivity$ThemeDelegate$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ MotionBackgroundDrawable f$0;

    public /* synthetic */ ChatActivity$ThemeDelegate$$ExternalSyntheticLambda1(MotionBackgroundDrawable motionBackgroundDrawable) {
        this.f$0 = motionBackgroundDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.setPatternIntensity(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}

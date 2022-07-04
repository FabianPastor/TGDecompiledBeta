package org.telegram.ui.Components.spoilers;

import android.animation.ValueAnimator;

public final /* synthetic */ class SpoilerEffect$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SpoilerEffect f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SpoilerEffect$$ExternalSyntheticLambda1(SpoilerEffect spoilerEffect, int i) {
        this.f$0 = spoilerEffect;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$startRipple$1(this.f$1, valueAnimator);
    }
}

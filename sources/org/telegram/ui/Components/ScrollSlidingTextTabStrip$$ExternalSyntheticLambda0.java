package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class ScrollSlidingTextTabStrip$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ScrollSlidingTextTabStrip f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ScrollSlidingTextTabStrip$$ExternalSyntheticLambda0(ScrollSlidingTextTabStrip scrollSlidingTextTabStrip, int i, int i2) {
        this.f$0 = scrollSlidingTextTabStrip;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4306x57ecd781(this.f$1, this.f$2, valueAnimator);
    }
}

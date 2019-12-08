package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PullForegroundDrawable$qtm8dTyBzhIxP_gaHq9EeLzdDNM implements AnimatorUpdateListener {
    private final /* synthetic */ PullForegroundDrawable f$0;

    public /* synthetic */ -$$Lambda$PullForegroundDrawable$qtm8dTyBzhIxP_gaHq9EeLzdDNM(PullForegroundDrawable pullForegroundDrawable) {
        this.f$0 = pullForegroundDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$colorize$3$PullForegroundDrawable(valueAnimator);
    }
}

package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PullForegroundDrawable$R-_cNvPCK3mSKIXSbZpOD-CpGhc implements AnimatorUpdateListener {
    private final /* synthetic */ PullForegroundDrawable f$0;

    public /* synthetic */ -$$Lambda$PullForegroundDrawable$R-_cNvPCK3mSKIXSbZpOD-CpGhc(PullForegroundDrawable pullForegroundDrawable) {
        this.f$0 = pullForegroundDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updateTextProgress$2$PullForegroundDrawable(valueAnimator);
    }
}

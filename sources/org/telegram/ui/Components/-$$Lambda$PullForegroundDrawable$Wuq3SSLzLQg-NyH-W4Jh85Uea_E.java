package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PullForegroundDrawable$Wuq3SSLzLQg-NyH-W4Jh85Uea_E implements AnimatorUpdateListener {
    private final /* synthetic */ PullForegroundDrawable f$0;

    public /* synthetic */ -$$Lambda$PullForegroundDrawable$Wuq3SSLzLQg-NyH-W4Jh85Uea_E(PullForegroundDrawable pullForegroundDrawable) {
        this.f$0 = pullForegroundDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$colorize$4$PullForegroundDrawable(valueAnimator);
    }
}

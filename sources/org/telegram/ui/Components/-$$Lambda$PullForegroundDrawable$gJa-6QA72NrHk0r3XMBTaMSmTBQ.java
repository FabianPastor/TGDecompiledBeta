package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PullForegroundDrawable$gJa-6QA72NrHk0r3XMBTaMSmTBQ implements AnimatorUpdateListener {
    private final /* synthetic */ PullForegroundDrawable f$0;

    public /* synthetic */ -$$Lambda$PullForegroundDrawable$gJa-6QA72NrHk0r3XMBTaMSmTBQ(PullForegroundDrawable pullForegroundDrawable) {
        this.f$0 = pullForegroundDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$startOutAnimation$7$PullForegroundDrawable(valueAnimator);
    }
}

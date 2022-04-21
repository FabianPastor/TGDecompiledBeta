package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;

public final /* synthetic */ class EllipsizeSpanAnimator$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ EllipsizeSpanAnimator f$0;
    public final /* synthetic */ EllipsizeSpanAnimator.TextAlphaSpan f$1;

    public /* synthetic */ EllipsizeSpanAnimator$$ExternalSyntheticLambda0(EllipsizeSpanAnimator ellipsizeSpanAnimator, EllipsizeSpanAnimator.TextAlphaSpan textAlphaSpan) {
        this.f$0 = ellipsizeSpanAnimator;
        this.f$1 = textAlphaSpan;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m3955x871035ba(this.f$1, valueAnimator);
    }
}

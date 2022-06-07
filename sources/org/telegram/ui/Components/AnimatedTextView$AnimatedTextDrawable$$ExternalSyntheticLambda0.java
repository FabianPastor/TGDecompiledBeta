package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.AnimatedTextView;

public final /* synthetic */ class AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ AnimatedTextView.AnimatedTextDrawable f$0;

    public /* synthetic */ AnimatedTextView$AnimatedTextDrawable$$ExternalSyntheticLambda0(AnimatedTextView.AnimatedTextDrawable animatedTextDrawable) {
        this.f$0 = animatedTextDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setText$3(valueAnimator);
    }
}

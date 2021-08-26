package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class TextSelectionHint$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ TextSelectionHint f$0;

    public /* synthetic */ TextSelectionHint$$ExternalSyntheticLambda2(TextSelectionHint textSelectionHint) {
        this.f$0 = textSelectionHint;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$show$2(valueAnimator);
    }
}

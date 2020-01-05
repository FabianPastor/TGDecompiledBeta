package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHint$s_OV-IqA7YUTjcamA5xS4TyprOg implements AnimatorUpdateListener {
    private final /* synthetic */ TextSelectionHint f$0;

    public /* synthetic */ -$$Lambda$TextSelectionHint$s_OV-IqA7YUTjcamA5xS4TyprOg(TextSelectionHint textSelectionHint) {
        this.f$0 = textSelectionHint;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$show$2$TextSelectionHint(valueAnimator);
    }
}

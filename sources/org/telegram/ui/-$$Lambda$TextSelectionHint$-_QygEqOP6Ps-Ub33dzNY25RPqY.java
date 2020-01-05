package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHint$-_QygEqOP6Ps-Ub33dzNY25RPqY implements AnimatorUpdateListener {
    private final /* synthetic */ TextSelectionHint f$0;

    public /* synthetic */ -$$Lambda$TextSelectionHint$-_QygEqOP6Ps-Ub33dzNY25RPqY(TextSelectionHint textSelectionHint) {
        this.f$0 = textSelectionHint;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$show$0$TextSelectionHint(valueAnimator);
    }
}

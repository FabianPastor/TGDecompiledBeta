package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHint$sI7lBFhVXBDD-5dKg9UCXZSUeH8 implements AnimatorUpdateListener {
    private final /* synthetic */ TextSelectionHint f$0;

    public /* synthetic */ -$$Lambda$TextSelectionHint$sI7lBFhVXBDD-5dKg9UCXZSUeH8(TextSelectionHint textSelectionHint) {
        this.f$0 = textSelectionHint;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$show$1$TextSelectionHint(valueAnimator);
    }
}

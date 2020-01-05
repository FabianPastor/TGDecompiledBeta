package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHint$Lo_7JsKX56d5uDIIkzycQH8QS_o implements AnimatorUpdateListener {
    private final /* synthetic */ TextSelectionHint f$0;

    public /* synthetic */ -$$Lambda$TextSelectionHint$Lo_7JsKX56d5uDIIkzycQH8QS_o(TextSelectionHint textSelectionHint) {
        this.f$0 = textSelectionHint;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$hideInternal$4$TextSelectionHint(valueAnimator);
    }
}

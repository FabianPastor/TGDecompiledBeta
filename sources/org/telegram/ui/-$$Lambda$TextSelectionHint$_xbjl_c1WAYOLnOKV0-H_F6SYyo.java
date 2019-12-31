package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHint$_xbjl_c1WAYOLnOKV0-H_F6SYyo implements AnimatorUpdateListener {
    private final /* synthetic */ TextSelectionHint f$0;

    public /* synthetic */ -$$Lambda$TextSelectionHint$_xbjl_c1WAYOLnOKV0-H_F6SYyo(TextSelectionHint textSelectionHint) {
        this.f$0 = textSelectionHint;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$show$3$TextSelectionHint(valueAnimator);
    }
}

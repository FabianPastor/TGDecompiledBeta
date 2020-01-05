package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TextSelectionHelper$DsXmQZvfGpCcORdBIu4jWbyY2gE implements AnimatorUpdateListener {
    private final /* synthetic */ TextSelectionHelper f$0;

    public /* synthetic */ -$$Lambda$TextSelectionHelper$DsXmQZvfGpCcORdBIu4jWbyY2gE(TextSelectionHelper textSelectionHelper) {
        this.f$0 = textSelectionHelper;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$showHandleViews$0$TextSelectionHelper(valueAnimator);
    }
}

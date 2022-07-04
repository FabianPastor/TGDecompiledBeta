package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class PipVideoOverlay$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PipVideoOverlay f$0;

    public /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda0(PipVideoOverlay pipVideoOverlay) {
        this.f$0 = pipVideoOverlay;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$toggleControls$6(valueAnimator);
    }
}

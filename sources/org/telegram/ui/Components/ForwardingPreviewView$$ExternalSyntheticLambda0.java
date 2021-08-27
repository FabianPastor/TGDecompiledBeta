package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class ForwardingPreviewView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ForwardingPreviewView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ ForwardingPreviewView$$ExternalSyntheticLambda0(ForwardingPreviewView forwardingPreviewView, int i, float f) {
        this.f$0 = forwardingPreviewView;
        this.f$1 = i;
        this.f$2 = f;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updatePositions$7(this.f$1, this.f$2, valueAnimator);
    }
}

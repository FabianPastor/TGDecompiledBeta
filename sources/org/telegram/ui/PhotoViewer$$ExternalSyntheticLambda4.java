package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda4 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda4(PhotoViewer photoViewer, int i, int i2) {
        this.f$0 = photoViewer;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateNavBarColorTo$42(this.f$1, this.f$2, valueAnimator);
    }
}

package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ImageReceiver f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda0(ImageReceiver imageReceiver) {
        this.f$0 = imageReceiver;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}

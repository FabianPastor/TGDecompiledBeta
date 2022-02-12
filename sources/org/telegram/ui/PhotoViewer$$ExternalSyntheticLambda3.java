package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda3 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda3(PhotoViewer photoViewer, boolean z) {
        this.f$0 = photoViewer;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setVideoPlayerControlVisible$46(this.f$1, valueAnimator);
    }
}

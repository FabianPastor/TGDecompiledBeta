package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda9 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda9(PhotoViewer photoViewer, boolean z) {
        this.f$0 = photoViewer;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setVideoPlayerControlVisible$52(this.f$1, valueAnimator);
    }
}

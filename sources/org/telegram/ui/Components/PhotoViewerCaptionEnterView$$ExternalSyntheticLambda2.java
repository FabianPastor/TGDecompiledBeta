package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class PhotoViewerCaptionEnterView$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PhotoViewerCaptionEnterView f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ PhotoViewerCaptionEnterView$$ExternalSyntheticLambda2(PhotoViewerCaptionEnterView photoViewerCaptionEnterView, float f) {
        this.f$0 = photoViewerCaptionEnterView;
        this.f$1 = f;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$showPopup$7(this.f$1, valueAnimator);
    }
}

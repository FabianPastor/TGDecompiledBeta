package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda33 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda33(PhotoViewer photoViewer, float f, float f2) {
        this.f$0 = photoViewer;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4245lambda$cropRotate$47$orgtelegramuiPhotoViewer(this.f$1, this.f$2, valueAnimator);
    }
}

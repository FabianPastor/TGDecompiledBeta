package org.telegram.ui.Components.Crop;

import android.animation.ValueAnimator;

public final /* synthetic */ class CropView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ CropView f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float[] f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;

    public /* synthetic */ CropView$$ExternalSyntheticLambda0(CropView cropView, float f, float[] fArr, float f2, float f3) {
        this.f$0 = cropView;
        this.f$1 = f;
        this.f$2 = fArr;
        this.f$3 = f2;
        this.f$4 = f3;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m898lambda$fillAreaView$0$orgtelegramuiComponentsCropCropView(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}

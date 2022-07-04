package org.telegram.ui.Components.Crop;

import android.animation.ValueAnimator;
import android.graphics.RectF;

public final /* synthetic */ class CropView$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ CropView f$0;
    public final /* synthetic */ RectF f$1;
    public final /* synthetic */ RectF f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ float f$5;
    public final /* synthetic */ float f$6;
    public final /* synthetic */ float f$7;

    public /* synthetic */ CropView$$ExternalSyntheticLambda2(CropView cropView, RectF rectF, RectF rectF2, float f, float f2, float f3, float f4, float f5) {
        this.f$0 = cropView;
        this.f$1 = rectF;
        this.f$2 = rectF2;
        this.f$3 = f;
        this.f$4 = f2;
        this.f$5 = f3;
        this.f$6 = f4;
        this.f$7 = f5;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m900lambda$maximize$2$orgtelegramuiComponentsCropCropView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, valueAnimator);
    }
}

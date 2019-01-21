package org.telegram.ui.Components.Crop;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class CropView$$Lambda$0 implements AnimatorUpdateListener {
    private final CropView arg$1;
    private final float arg$2;
    private final float[] arg$3;
    private final float arg$4;
    private final float arg$5;

    CropView$$Lambda$0(CropView cropView, float f, float[] fArr, float f2, float f3) {
        this.arg$1 = cropView;
        this.arg$2 = f;
        this.arg$3 = fArr;
        this.arg$4 = f2;
        this.arg$5 = f3;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.arg$1.lambda$fillAreaView$0$CropView(this.arg$2, this.arg$3, this.arg$4, this.arg$5, valueAnimator);
    }
}

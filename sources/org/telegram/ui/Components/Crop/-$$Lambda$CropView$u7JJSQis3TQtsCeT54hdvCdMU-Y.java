package org.telegram.ui.Components.Crop;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CropView$u7JJSQis3TQtsCeT54hdvCdMU-Y implements AnimatorUpdateListener {
    private final /* synthetic */ CropView f$0;
    private final /* synthetic */ float f$1;
    private final /* synthetic */ float[] f$2;
    private final /* synthetic */ float f$3;
    private final /* synthetic */ float f$4;

    public /* synthetic */ -$$Lambda$CropView$u7JJSQis3TQtsCeT54hdvCdMU-Y(CropView cropView, float f, float[] fArr, float f2, float f3) {
        this.f$0 = cropView;
        this.f$1 = f;
        this.f$2 = fArr;
        this.f$3 = f2;
        this.f$4 = f3;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$fillAreaView$0$CropView(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}

package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.CubicBezierInterpolator;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda8 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PhotoViewer f$0;
    public final /* synthetic */ CubicBezierInterpolator f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ float f$5;
    public final /* synthetic */ float f$6;
    public final /* synthetic */ float f$7;
    public final /* synthetic */ float f$8;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda8(PhotoViewer photoViewer, CubicBezierInterpolator cubicBezierInterpolator, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        this.f$0 = photoViewer;
        this.f$1 = cubicBezierInterpolator;
        this.f$2 = f;
        this.f$3 = f2;
        this.f$4 = f3;
        this.f$5 = f4;
        this.f$6 = f5;
        this.f$7 = f6;
        this.f$8 = f7;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$switchToPip$46(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, valueAnimator);
    }
}

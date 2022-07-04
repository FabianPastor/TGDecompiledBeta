package org.telegram.ui.Components.Premium;

import android.animation.ValueAnimator;

public final /* synthetic */ class CarouselView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ CarouselView f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ CarouselView$$ExternalSyntheticLambda0(CarouselView carouselView, float f, float f2) {
        this.f$0 = carouselView;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$scrollToInternal$2(this.f$1, this.f$2, valueAnimator);
    }
}

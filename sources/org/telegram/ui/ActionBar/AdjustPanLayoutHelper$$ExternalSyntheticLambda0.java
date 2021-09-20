package org.telegram.ui.ActionBar;

import android.animation.ValueAnimator;

public final /* synthetic */ class AdjustPanLayoutHelper$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ AdjustPanLayoutHelper f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ AdjustPanLayoutHelper$$ExternalSyntheticLambda0(AdjustPanLayoutHelper adjustPanLayoutHelper, float f, float f2, boolean z) {
        this.f$0 = adjustPanLayoutHelper;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateHeight$0(this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}

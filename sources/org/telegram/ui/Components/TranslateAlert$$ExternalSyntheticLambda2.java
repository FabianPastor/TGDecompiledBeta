package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class TranslateAlert$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ TranslateAlert f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ TranslateAlert$$ExternalSyntheticLambda2(TranslateAlert translateAlert, int i) {
        this.f$0 = translateAlert;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$new$3(this.f$1, valueAnimator);
    }
}

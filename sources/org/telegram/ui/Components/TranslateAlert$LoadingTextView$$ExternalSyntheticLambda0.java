package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class TranslateAlert$LoadingTextView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ TranslateAlert.LoadingTextView f$0;

    public /* synthetic */ TranslateAlert$LoadingTextView$$ExternalSyntheticLambda0(TranslateAlert.LoadingTextView loadingTextView) {
        this.f$0 = loadingTextView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setText$2(valueAnimator);
    }
}

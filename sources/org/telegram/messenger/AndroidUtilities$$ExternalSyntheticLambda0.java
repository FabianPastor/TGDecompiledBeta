package org.telegram.messenger;

import android.animation.ValueAnimator;
import android.view.Window;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ Window f$0;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda0(Window window) {
        this.f$0 = window;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.setNavigationBarColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}

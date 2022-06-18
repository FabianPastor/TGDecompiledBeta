package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.CrossfadeDrawable;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda18 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ CrossfadeDrawable f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda18(CrossfadeDrawable crossfadeDrawable) {
        this.f$0 = crossfadeDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}

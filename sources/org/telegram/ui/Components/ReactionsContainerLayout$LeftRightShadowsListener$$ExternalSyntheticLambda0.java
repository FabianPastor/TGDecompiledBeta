package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import androidx.core.util.Consumer;

public final /* synthetic */ class ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda0(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.accept((Float) valueAnimator.getAnimatedValue());
    }
}

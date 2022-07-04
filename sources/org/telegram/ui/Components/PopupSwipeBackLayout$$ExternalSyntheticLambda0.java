package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class PopupSwipeBackLayout$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PopupSwipeBackLayout f$0;

    public /* synthetic */ PopupSwipeBackLayout$$ExternalSyntheticLambda0(PopupSwipeBackLayout popupSwipeBackLayout) {
        this.f$0 = popupSwipeBackLayout;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setNewForegroundHeight$1(valueAnimator);
    }
}

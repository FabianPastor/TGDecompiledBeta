package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda5 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda5(ChatActivityEnterView chatActivityEnterView, int i) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setStickersExpanded$40(this.f$1, valueAnimator);
    }
}
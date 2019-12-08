package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$lJSCRzweiAfnlNbwBsdYjoBikIs implements AnimatorUpdateListener {
    private final /* synthetic */ ChatActivityEnterView f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$lJSCRzweiAfnlNbwBsdYjoBikIs(ChatActivityEnterView chatActivityEnterView, int i) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setStickersExpanded$31$ChatActivityEnterView(this.f$1, valueAnimator);
    }
}

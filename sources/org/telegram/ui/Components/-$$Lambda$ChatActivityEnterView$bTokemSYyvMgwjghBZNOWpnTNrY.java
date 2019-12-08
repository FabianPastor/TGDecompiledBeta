package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$bTokemSYyvMgwjghBZNOWpnTNrY implements AnimatorUpdateListener {
    private final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$bTokemSYyvMgwjghBZNOWpnTNrY(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$checkStickresExpandHeight$28$ChatActivityEnterView(valueAnimator);
    }
}

package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class ChatActivityEnterView$$Lambda$18 implements AnimatorUpdateListener {
    private final ChatActivityEnterView arg$1;

    ChatActivityEnterView$$Lambda$18(ChatActivityEnterView chatActivityEnterView) {
        this.arg$1 = chatActivityEnterView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.arg$1.lambda$checkStickresExpandHeight$18$ChatActivityEnterView(valueAnimator);
    }
}

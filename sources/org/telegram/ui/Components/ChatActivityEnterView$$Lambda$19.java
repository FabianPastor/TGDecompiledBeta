package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class ChatActivityEnterView$$Lambda$19 implements AnimatorUpdateListener {
    private final ChatActivityEnterView arg$1;

    ChatActivityEnterView$$Lambda$19(ChatActivityEnterView chatActivityEnterView) {
        this.arg$1 = chatActivityEnterView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.arg$1.lambda$checkStickresExpandHeight$19$ChatActivityEnterView(valueAnimator);
    }
}

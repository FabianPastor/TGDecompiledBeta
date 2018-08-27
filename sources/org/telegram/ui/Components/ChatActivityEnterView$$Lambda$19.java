package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class ChatActivityEnterView$$Lambda$19 implements AnimatorUpdateListener {
    private final ChatActivityEnterView arg$1;
    private final int arg$2;

    ChatActivityEnterView$$Lambda$19(ChatActivityEnterView chatActivityEnterView, int i) {
        this.arg$1 = chatActivityEnterView;
        this.arg$2 = i;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.arg$1.lambda$setStickersExpanded$19$ChatActivityEnterView(this.arg$2, valueAnimator);
    }
}

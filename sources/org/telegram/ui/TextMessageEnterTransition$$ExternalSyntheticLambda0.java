package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class TextMessageEnterTransition$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ TextMessageEnterTransition f$0;
    public final /* synthetic */ ChatActivityEnterView f$1;
    public final /* synthetic */ MessageEnterTransitionContainer f$2;

    public /* synthetic */ TextMessageEnterTransition$$ExternalSyntheticLambda0(TextMessageEnterTransition textMessageEnterTransition, ChatActivityEnterView chatActivityEnterView, MessageEnterTransitionContainer messageEnterTransitionContainer) {
        this.f$0 = textMessageEnterTransition;
        this.f$1 = chatActivityEnterView;
        this.f$2 = messageEnterTransitionContainer;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4651lambda$new$0$orgtelegramuiTextMessageEnterTransition(this.f$1, this.f$2, valueAnimator);
    }
}

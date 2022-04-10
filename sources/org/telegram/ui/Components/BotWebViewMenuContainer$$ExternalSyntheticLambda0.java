package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class BotWebViewMenuContainer$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BotWebViewMenuContainer f$0;
    public final /* synthetic */ ChatActivityBotWebViewButton f$1;

    public /* synthetic */ BotWebViewMenuContainer$$ExternalSyntheticLambda0(BotWebViewMenuContainer botWebViewMenuContainer, ChatActivityBotWebViewButton chatActivityBotWebViewButton) {
        this.f$0 = botWebViewMenuContainer;
        this.f$1 = chatActivityBotWebViewButton;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateBotButton$6(this.f$1, valueAnimator);
    }
}

package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;

public final /* synthetic */ class BotWebViewMenuContainer$$ExternalSyntheticLambda7 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ BotWebViewMenuContainer f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ChatActivityBotWebViewButton f$2;

    public /* synthetic */ BotWebViewMenuContainer$$ExternalSyntheticLambda7(BotWebViewMenuContainer botWebViewMenuContainer, boolean z, ChatActivityBotWebViewButton chatActivityBotWebViewButton) {
        this.f$0 = botWebViewMenuContainer;
        this.f$1 = z;
        this.f$2 = chatActivityBotWebViewButton;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$animateBotButton$13(this.f$1, this.f$2, dynamicAnimation, z, f, f2);
    }
}

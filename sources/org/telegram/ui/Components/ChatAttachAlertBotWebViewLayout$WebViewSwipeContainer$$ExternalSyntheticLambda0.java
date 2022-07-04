package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0(ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer, float f) {
        this.f$0 = webViewSwipeContainer;
        this.f$1 = f;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.m773x81ee602b(this.f$1, dynamicAnimation, z, f, f2);
    }
}

package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda1 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda1(ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer, Runnable runnable) {
        this.f$0 = webViewSwipeContainer;
        this.f$1 = runnable;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.m774x212eaa3d(this.f$1, dynamicAnimation, z, f, f2);
    }
}

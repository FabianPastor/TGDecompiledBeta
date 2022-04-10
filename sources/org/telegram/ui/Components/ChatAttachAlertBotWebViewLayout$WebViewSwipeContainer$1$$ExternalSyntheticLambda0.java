package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$1$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.AnonymousClass1 f$0;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$1$$ExternalSyntheticLambda0(ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.AnonymousClass1 r1) {
        this.f$0 = r1;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.lambda$onFling$0(dynamicAnimation, z, f, f2);
    }
}

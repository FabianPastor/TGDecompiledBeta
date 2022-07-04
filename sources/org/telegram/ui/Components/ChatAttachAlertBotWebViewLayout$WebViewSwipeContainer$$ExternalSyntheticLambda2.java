package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda2 implements DynamicAnimation.OnAnimationUpdateListener {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ float f$4;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda2(ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer, float f, float f2, boolean z, float f3) {
        this.f$0 = webViewSwipeContainer;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = z;
        this.f$4 = f3;
    }

    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.f$0.m772x533cvar_c(this.f$1, this.f$2, this.f$3, this.f$4, dynamicAnimation, f, f2);
    }
}

package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0(ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer, boolean z) {
        this.f$0 = webViewSwipeContainer;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m3814x248b8bed(this.f$1, valueAnimator);
    }
}

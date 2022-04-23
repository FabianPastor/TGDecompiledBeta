package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;

public final /* synthetic */ class ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;

    public /* synthetic */ ChatAttachAlertBotWebViewLayout$WebViewSwipeContainer$$ExternalSyntheticLambda0(ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer, boolean z, float f, float f2) {
        this.f$0 = webViewSwipeContainer;
        this.f$1 = z;
        this.f$2 = f;
        this.f$3 = f2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setOffsetY$0(this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}

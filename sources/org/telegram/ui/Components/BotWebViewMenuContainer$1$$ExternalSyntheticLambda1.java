package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.BotWebViewMenuContainer;

public final /* synthetic */ class BotWebViewMenuContainer$1$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BotWebViewMenuContainer.AnonymousClass1 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ BotWebViewMenuContainer$1$$ExternalSyntheticLambda1(BotWebViewMenuContainer.AnonymousClass1 r1, int i, int i2) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onWebAppSetBackgroundColor$1(this.f$1, this.f$2, valueAnimator);
    }
}

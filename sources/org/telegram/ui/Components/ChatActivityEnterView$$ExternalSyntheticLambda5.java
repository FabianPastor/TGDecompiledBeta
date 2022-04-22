package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda5 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda5(ChatActivityEnterView chatActivityEnterView, float f, float f2, float f3, float f4) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = f3;
        this.f$4 = f4;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updateSendAsButton$44(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}

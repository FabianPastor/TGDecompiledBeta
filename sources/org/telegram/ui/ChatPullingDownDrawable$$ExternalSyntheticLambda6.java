package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class ChatPullingDownDrawable$$ExternalSyntheticLambda6 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatPullingDownDrawable f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ ChatPullingDownDrawable$$ExternalSyntheticLambda6(ChatPullingDownDrawable chatPullingDownDrawable, View view) {
        this.f$0 = chatPullingDownDrawable;
        this.f$1 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m3269xdd19491f(this.f$1, valueAnimator);
    }
}

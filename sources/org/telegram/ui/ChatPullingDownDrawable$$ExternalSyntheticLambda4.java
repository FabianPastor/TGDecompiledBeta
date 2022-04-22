package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class ChatPullingDownDrawable$$ExternalSyntheticLambda4 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatPullingDownDrawable f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ ChatPullingDownDrawable$$ExternalSyntheticLambda4(ChatPullingDownDrawable chatPullingDownDrawable, View view) {
        this.f$0 = chatPullingDownDrawable;
        this.f$1 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$showReleaseState$1(this.f$1, valueAnimator);
    }
}

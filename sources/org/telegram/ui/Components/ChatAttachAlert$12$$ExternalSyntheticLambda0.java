package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class ChatAttachAlert$12$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ EditTextCaption f$0;

    public /* synthetic */ ChatAttachAlert$12$$ExternalSyntheticLambda0(EditTextCaption editTextCaption) {
        this.f$0 = editTextCaption;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}

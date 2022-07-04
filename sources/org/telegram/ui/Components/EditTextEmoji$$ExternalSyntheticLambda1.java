package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class EditTextEmoji$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ EditTextEmoji f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ EditTextEmoji$$ExternalSyntheticLambda1(EditTextEmoji editTextEmoji, int i) {
        this.f$0 = editTextEmoji;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m916lambda$hidePopup$1$orgtelegramuiComponentsEditTextEmoji(this.f$1, valueAnimator);
    }
}

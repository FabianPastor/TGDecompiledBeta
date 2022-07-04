package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class VoiceMessageEnterTransition$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ VoiceMessageEnterTransition f$0;
    public final /* synthetic */ MessageEnterTransitionContainer f$1;

    public /* synthetic */ VoiceMessageEnterTransition$$ExternalSyntheticLambda0(VoiceMessageEnterTransition voiceMessageEnterTransition, MessageEnterTransitionContainer messageEnterTransitionContainer) {
        this.f$0 = voiceMessageEnterTransition;
        this.f$1 = messageEnterTransitionContainer;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4836lambda$new$0$orgtelegramuiVoiceMessageEnterTransition(this.f$1, valueAnimator);
    }
}

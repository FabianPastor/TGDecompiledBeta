package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class MessageContainsEmojiButton$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ MessageContainsEmojiButton f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ MessageContainsEmojiButton$$ExternalSyntheticLambda0(MessageContainsEmojiButton messageContainsEmojiButton, boolean z) {
        this.f$0 = messageContainsEmojiButton;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$didReceivedNotification$0(this.f$1, valueAnimator);
    }
}

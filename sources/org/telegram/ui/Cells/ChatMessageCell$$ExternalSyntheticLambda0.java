package org.telegram.ui.Cells;

import android.animation.ValueAnimator;

public final /* synthetic */ class ChatMessageCell$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ChatMessageCell f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ChatMessageCell$$ExternalSyntheticLambda0(ChatMessageCell chatMessageCell, boolean z) {
        this.f$0 = chatMessageCell;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$createStatusDrawableAnimator$1(this.f$1, valueAnimator);
    }
}
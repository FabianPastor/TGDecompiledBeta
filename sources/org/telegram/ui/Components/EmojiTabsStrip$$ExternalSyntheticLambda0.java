package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class EmojiTabsStrip$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ EmojiTabsStrip f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ EmojiTabsStrip$$ExternalSyntheticLambda0(EmojiTabsStrip emojiTabsStrip, float f, float f2) {
        this.f$0 = emojiTabsStrip;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$select$2(this.f$1, this.f$2, valueAnimator);
    }
}

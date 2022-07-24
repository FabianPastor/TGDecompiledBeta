package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.EmojiTabsStrip;

public final /* synthetic */ class EmojiTabsStrip$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ EmojiTabsStrip f$0;
    public final /* synthetic */ EmojiTabsStrip.EmojiTabButton f$1;

    public /* synthetic */ EmojiTabsStrip$$ExternalSyntheticLambda1(EmojiTabsStrip emojiTabsStrip, EmojiTabsStrip.EmojiTabButton emojiTabButton) {
        this.f$0 = emojiTabsStrip;
        this.f$1 = emojiTabButton;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updateEmojiPacks$0(this.f$1, valueAnimator);
    }
}

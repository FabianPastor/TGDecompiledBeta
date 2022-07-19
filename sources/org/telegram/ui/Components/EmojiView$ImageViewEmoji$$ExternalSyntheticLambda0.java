package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$ImageViewEmoji$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ EmojiView.ImageViewEmoji f$0;

    public /* synthetic */ EmojiView$ImageViewEmoji$$ExternalSyntheticLambda0(EmojiView.ImageViewEmoji imageViewEmoji) {
        this.f$0 = imageViewEmoji;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setPressed$1(valueAnimator);
    }
}

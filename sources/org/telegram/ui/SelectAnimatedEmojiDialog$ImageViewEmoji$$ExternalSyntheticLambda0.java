package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public final /* synthetic */ class SelectAnimatedEmojiDialog$ImageViewEmoji$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SelectAnimatedEmojiDialog.ImageViewEmoji f$0;

    public /* synthetic */ SelectAnimatedEmojiDialog$ImageViewEmoji$$ExternalSyntheticLambda0(SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji) {
        this.f$0 = imageViewEmoji;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setPressed$0(valueAnimator);
    }
}

package org.telegram.ui;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public final /* synthetic */ class SelectAnimatedEmojiDialog$$ExternalSyntheticLambda3 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SelectAnimatedEmojiDialog f$0;
    public final /* synthetic */ Rect f$1;
    public final /* synthetic */ Rect f$2;
    public final /* synthetic */ SelectAnimatedEmojiDialog.ImageViewEmoji f$3;
    public final /* synthetic */ boolean[] f$4;
    public final /* synthetic */ Runnable f$5;
    public final /* synthetic */ AnimatedEmojiDrawable f$6;

    public /* synthetic */ SelectAnimatedEmojiDialog$$ExternalSyntheticLambda3(SelectAnimatedEmojiDialog selectAnimatedEmojiDialog, Rect rect, Rect rect2, SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji, boolean[] zArr, Runnable runnable, AnimatedEmojiDrawable animatedEmojiDrawable) {
        this.f$0 = selectAnimatedEmojiDialog;
        this.f$1 = rect;
        this.f$2 = rect2;
        this.f$3 = imageViewEmoji;
        this.f$4 = zArr;
        this.f$5 = runnable;
        this.f$6 = animatedEmojiDrawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateEmojiSelect$5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, valueAnimator);
    }
}

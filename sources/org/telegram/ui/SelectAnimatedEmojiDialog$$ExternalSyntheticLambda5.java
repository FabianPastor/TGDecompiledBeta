package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class SelectAnimatedEmojiDialog$$ExternalSyntheticLambda5 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SelectAnimatedEmojiDialog f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ SelectAnimatedEmojiDialog$$ExternalSyntheticLambda5(SelectAnimatedEmojiDialog selectAnimatedEmojiDialog, boolean z) {
        this.f$0 = selectAnimatedEmojiDialog;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$switchSearchEmptyView$8(this.f$1, valueAnimator);
    }
}

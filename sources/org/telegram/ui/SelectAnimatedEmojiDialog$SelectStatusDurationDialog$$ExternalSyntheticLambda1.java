package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public final /* synthetic */ class SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ SelectAnimatedEmojiDialog.SelectStatusDurationDialog f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Runnable f$3;
    public final /* synthetic */ boolean[] f$4;

    public /* synthetic */ SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda1(SelectAnimatedEmojiDialog.SelectStatusDurationDialog selectStatusDurationDialog, boolean z, boolean z2, Runnable runnable, boolean[] zArr) {
        this.f$0 = selectStatusDurationDialog;
        this.f$1 = z;
        this.f$2 = z2;
        this.f$3 = runnable;
        this.f$4 = zArr;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateShow$10(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}

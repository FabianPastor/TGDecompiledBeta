package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda2(GroupCallActivity groupCallActivity, float f, float f2, float f3, int i) {
        this.f$0 = groupCallActivity;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = f3;
        this.f$4 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$runAvatarPreviewTransition$61(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}

package org.telegram.ui.Components.voip;

import android.animation.ValueAnimator;

public final /* synthetic */ class GroupCallRenderersContainer$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GroupCallRenderersContainer f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;

    public /* synthetic */ GroupCallRenderersContainer$$ExternalSyntheticLambda2(GroupCallRenderersContainer groupCallRenderersContainer, float f, float f2, float f3) {
        this.f$0 = groupCallRenderersContainer;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = f3;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m1588x102cdcf5(this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}

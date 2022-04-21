package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda33 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ View f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda33(GroupCallActivity groupCallActivity, float f, View view) {
        this.f$0 = groupCallActivity;
        this.f$1 = f;
        this.f$2 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m2232lambda$updateMuteButton$49$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, valueAnimator);
    }
}

package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda3 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ View f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda3(GroupCallActivity groupCallActivity, float f, View view) {
        this.f$0 = groupCallActivity;
        this.f$1 = f;
        this.f$2 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updateMuteButton$49(this.f$1, this.f$2, valueAnimator);
    }
}

package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ ValueAnimator f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda2(ProfileActivity profileActivity, ValueAnimator valueAnimator, float f, boolean z) {
        this.f$0 = profileActivity;
        this.f$1 = valueAnimator;
        this.f$2 = f;
        this.f$3 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$searchExpandTransition$28(this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}

package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda5 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ LoginActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda5(LoginActivity loginActivity, boolean z) {
        this.f$0 = loginActivity;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$showEditDoneProgress$15(this.f$1, valueAnimator);
    }
}

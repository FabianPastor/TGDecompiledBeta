package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ LoginActivity.PhoneNumberConfirmView f$0;

    public /* synthetic */ LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda2(LoginActivity.PhoneNumberConfirmView phoneNumberConfirmView) {
        this.f$0 = phoneNumberConfirmView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$animateProgress$5(valueAnimator);
    }
}

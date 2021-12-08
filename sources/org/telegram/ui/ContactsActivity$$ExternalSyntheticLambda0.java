package org.telegram.ui;

import android.animation.ValueAnimator;
import android.view.ViewGroup;

public final /* synthetic */ class ContactsActivity$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ValueAnimator f$0;
    public final /* synthetic */ ViewGroup f$1;

    public /* synthetic */ ContactsActivity$$ExternalSyntheticLambda0(ValueAnimator valueAnimator, ViewGroup viewGroup) {
        this.f$0 = valueAnimator;
        this.f$1 = viewGroup;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        ContactsActivity.lambda$onCustomTransitionAnimation$7(this.f$0, this.f$1, valueAnimator);
    }
}

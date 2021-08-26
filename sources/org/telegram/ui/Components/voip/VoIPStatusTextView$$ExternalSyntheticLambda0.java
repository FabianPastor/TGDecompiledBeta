package org.telegram.ui.Components.voip;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class VoIPStatusTextView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ View f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ VoIPStatusTextView$$ExternalSyntheticLambda0(View view, View view2) {
        this.f$0 = view;
        this.f$1 = view2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        VoIPStatusTextView.lambda$replaceViews$1(this.f$0, this.f$1, valueAnimator);
    }
}

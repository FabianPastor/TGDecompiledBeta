package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda11 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ float f$0;
    public final /* synthetic */ FrameLayout f$1;
    public final /* synthetic */ LinearLayout f$2;
    public final /* synthetic */ View f$3;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda11(float f, FrameLayout frameLayout, LinearLayout linearLayout, View view) {
        this.f$0 = f;
        this.f$1 = frameLayout;
        this.f$2 = linearLayout;
        this.f$3 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        ChatActivityEnterView.lambda$new$13(this.f$0, this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}

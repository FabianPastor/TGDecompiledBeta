package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.ActionBar.ActionBarMenu;

public final /* synthetic */ class ChatActivity$36$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ActionBarMenu f$0;

    public /* synthetic */ ChatActivity$36$$ExternalSyntheticLambda0(ActionBarMenu actionBarMenu) {
        this.f$0 = actionBarMenu;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.translateXItems(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}

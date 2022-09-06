package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.ActionBar.ActionBarMenu;

public final /* synthetic */ class ChatActivity$37$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ActionBarMenu f$0;

    public /* synthetic */ ChatActivity$37$$ExternalSyntheticLambda1(ActionBarMenu actionBarMenu) {
        this.f$0 = actionBarMenu;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.translateXItems(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }
}

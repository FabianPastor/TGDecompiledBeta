package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsActivity$EJXNvP6nkyb7IfXTcb9m9EC-Uw4 implements AnimatorUpdateListener {
    private final /* synthetic */ DialogsActivity f$0;

    public /* synthetic */ -$$Lambda$DialogsActivity$EJXNvP6nkyb7IfXTcb9m9EC-Uw4(DialogsActivity dialogsActivity) {
        this.f$0 = dialogsActivity;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$hideFloatingButton$20$DialogsActivity(valueAnimator);
    }
}

package org.telegram.messenger;

import android.animation.ValueAnimator;
import android.view.Window;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ AndroidUtilities.IntColorCallback f$0;
    public final /* synthetic */ Window f$1;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda1(AndroidUtilities.IntColorCallback intColorCallback, Window window) {
        this.f$0 = intColorCallback;
        this.f$1 = window;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        AndroidUtilities.lambda$setNavigationBarColor$12(this.f$0, this.f$1, valueAnimator);
    }
}

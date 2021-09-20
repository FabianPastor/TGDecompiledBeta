package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.Bulletin;

public final /* synthetic */ class Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda0(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        Bulletin.Layout.SpringTransition.lambda$animateExit$2(this.f$0, dynamicAnimation, z, f, f2);
    }
}

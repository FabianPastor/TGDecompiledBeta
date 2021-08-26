package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.Bulletin;

public final /* synthetic */ class Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda1 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ Bulletin.Layout f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda1(Bulletin.Layout layout, Runnable runnable) {
        this.f$0 = layout;
        this.f$1 = runnable;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        Bulletin.Layout.SpringTransition.lambda$animateEnter$0(this.f$0, this.f$1, dynamicAnimation, z, f, f2);
    }
}

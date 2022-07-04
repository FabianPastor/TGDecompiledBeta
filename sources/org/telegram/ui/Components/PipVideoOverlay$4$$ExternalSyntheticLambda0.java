package org.telegram.ui.Components;

import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.PipVideoOverlay;

public final /* synthetic */ class PipVideoOverlay$4$$ExternalSyntheticLambda0 implements DynamicAnimation.OnAnimationEndListener {
    public final /* synthetic */ PipVideoOverlay.AnonymousClass4 f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ PipVideoOverlay$4$$ExternalSyntheticLambda0(PipVideoOverlay.AnonymousClass4 r1, float f) {
        this.f$0 = r1;
        this.f$1 = f;
    }

    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.f$0.m1219lambda$onScroll$0$orgtelegramuiComponentsPipVideoOverlay$4(this.f$1, dynamicAnimation, z, f, f2);
    }
}

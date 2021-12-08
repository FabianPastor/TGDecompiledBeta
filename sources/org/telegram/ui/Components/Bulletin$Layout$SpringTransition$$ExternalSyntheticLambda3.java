package org.telegram.ui.Components;

import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.DynamicAnimation;
import org.telegram.ui.Components.Bulletin;

public final /* synthetic */ class Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda3 implements DynamicAnimation.OnAnimationUpdateListener {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ Bulletin.Layout f$1;

    public /* synthetic */ Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda3(Consumer consumer, Bulletin.Layout layout) {
        this.f$0 = consumer;
        this.f$1 = layout;
    }

    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.f$0.accept(Float.valueOf(this.f$1.getTranslationY()));
    }
}

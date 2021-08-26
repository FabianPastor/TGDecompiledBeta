package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import androidx.core.util.Consumer;
import org.telegram.ui.Components.Bulletin;

public final /* synthetic */ class Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ Bulletin.Layout f$1;

    public /* synthetic */ Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda0(Consumer consumer, Bulletin.Layout layout) {
        this.f$0 = consumer;
        this.f$1 = layout;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.accept(Float.valueOf(this.f$1.getTranslationY()));
    }
}

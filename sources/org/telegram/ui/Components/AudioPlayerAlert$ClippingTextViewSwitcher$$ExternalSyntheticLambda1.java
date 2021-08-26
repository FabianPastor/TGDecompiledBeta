package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.AudioPlayerAlert;

public final /* synthetic */ class AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ AudioPlayerAlert.ClippingTextViewSwitcher f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda1(AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher, int i) {
        this.f$0 = clippingTextViewSwitcher;
        this.f$1 = i;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setText$1(this.f$1, valueAnimator);
    }
}

package org.telegram.ui.Components;

import android.animation.ValueAnimator;

public final /* synthetic */ class WaveDrawable$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ WaveDrawable f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ float f$5;
    public final /* synthetic */ float f$6;

    public /* synthetic */ WaveDrawable$$ExternalSyntheticLambda1(WaveDrawable waveDrawable, float f, float f2, float f3, float f4, float f5, float f6) {
        this.f$0 = waveDrawable;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = f3;
        this.f$4 = f4;
        this.f$5 = f5;
        this.f$6 = f6;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4527lambda$setValue$1$orgtelegramuiComponentsWaveDrawable(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, valueAnimator);
    }
}

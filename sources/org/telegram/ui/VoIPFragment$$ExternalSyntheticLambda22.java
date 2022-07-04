package org.telegram.ui;

import android.animation.ValueAnimator;

public final /* synthetic */ class VoIPFragment$$ExternalSyntheticLambda22 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ VoIPFragment f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;

    public /* synthetic */ VoIPFragment$$ExternalSyntheticLambda22(VoIPFragment voIPFragment, float f, float f2, float f3) {
        this.f$0 = voIPFragment;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = f3;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4813lambda$finishZoom$12$orgtelegramuiVoIPFragment(this.f$1, this.f$2, this.f$3, valueAnimator);
    }
}

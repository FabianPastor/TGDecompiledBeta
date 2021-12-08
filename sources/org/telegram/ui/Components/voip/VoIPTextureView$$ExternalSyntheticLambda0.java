package org.telegram.ui.Components.voip;

import android.animation.ValueAnimator;

public final /* synthetic */ class VoIPTextureView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ VoIPTextureView f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ float f$5;

    public /* synthetic */ VoIPTextureView$$ExternalSyntheticLambda0(VoIPTextureView voIPTextureView, float f, float f2, float f3, float f4, float f5) {
        this.f$0 = voIPTextureView;
        this.f$1 = f;
        this.f$2 = f2;
        this.f$3 = f3;
        this.f$4 = f4;
        this.f$5 = f5;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m2774x20db84c2(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, valueAnimator);
    }
}

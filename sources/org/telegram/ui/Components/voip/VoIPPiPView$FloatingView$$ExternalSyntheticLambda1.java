package org.telegram.ui.Components.voip;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.voip.VoIPPiPView;

public final /* synthetic */ class VoIPPiPView$FloatingView$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ float f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ VoIPPiPView f$2;

    public /* synthetic */ VoIPPiPView$FloatingView$$ExternalSyntheticLambda1(float f, float f2, VoIPPiPView voIPPiPView) {
        this.f$0 = f;
        this.f$1 = f2;
        this.f$2 = voIPPiPView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        VoIPPiPView.FloatingView.lambda$expand$0(this.f$0, this.f$1, this.f$2, valueAnimator);
    }
}

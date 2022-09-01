package org.telegram.ui.Components.Premium;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;

public final /* synthetic */ class PremiumPreviewBottomSheet$4$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PremiumPreviewBottomSheet.AnonymousClass4 f$0;
    public final /* synthetic */ Drawable f$1;

    public /* synthetic */ PremiumPreviewBottomSheet$4$$ExternalSyntheticLambda0(PremiumPreviewBottomSheet.AnonymousClass4 r1, Drawable drawable) {
        this.f$0 = r1;
        this.f$1 = drawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$onAnimationEnd$0(this.f$1, valueAnimator);
    }
}

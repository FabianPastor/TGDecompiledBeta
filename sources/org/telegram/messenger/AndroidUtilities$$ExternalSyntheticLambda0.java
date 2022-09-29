package org.telegram.messenger;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import java.util.concurrent.atomic.AtomicBoolean;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ImageView f$0;
    public final /* synthetic */ AtomicBoolean f$1;
    public final /* synthetic */ Drawable f$2;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda0(ImageView imageView, AtomicBoolean atomicBoolean, Drawable drawable) {
        this.f$0 = imageView;
        this.f$1 = atomicBoolean;
        this.f$2 = drawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        AndroidUtilities.lambda$updateImageViewImageAnimated$15(this.f$0, this.f$1, this.f$2, valueAnimator);
    }
}

package org.telegram.messenger;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.VideoPlayer;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ VideoPlayer f$1;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda0(MediaController mediaController, VideoPlayer videoPlayer) {
        this.f$0 = mediaController;
        this.f$1 = videoPlayer;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$cleanupPlayer$10(this.f$1, valueAnimator);
    }
}

package org.telegram.messenger;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.VideoPlayer;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda10 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ VideoPlayer f$1;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda10(MediaController mediaController, VideoPlayer videoPlayer) {
        this.f$0 = mediaController;
        this.f$1 = videoPlayer;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m87lambda$cleanupPlayer$10$orgtelegrammessengerMediaController(this.f$1, valueAnimator);
    }
}

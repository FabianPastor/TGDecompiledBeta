package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.AudioPlayerAlert;

public final /* synthetic */ class AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BackupImageView f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda1(BackupImageView backupImageView, boolean z) {
        this.f$0 = backupImageView;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        AudioPlayerAlert.CoverContainer.lambda$switchImageViews$1(this.f$0, this.f$1, valueAnimator);
    }
}

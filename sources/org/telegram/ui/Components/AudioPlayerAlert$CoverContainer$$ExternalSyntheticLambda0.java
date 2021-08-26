package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.AudioPlayerAlert;

public final /* synthetic */ class AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BackupImageView f$0;
    public final /* synthetic */ BackupImageView f$1;

    public /* synthetic */ AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda0(BackupImageView backupImageView, BackupImageView backupImageView2) {
        this.f$0 = backupImageView;
        this.f$1 = backupImageView2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        AudioPlayerAlert.CoverContainer.lambda$switchImageViews$2(this.f$0, this.f$1, valueAnimator);
    }
}

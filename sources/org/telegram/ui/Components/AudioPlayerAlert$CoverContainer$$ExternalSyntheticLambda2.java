package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;
import org.telegram.ui.Components.AudioPlayerAlert;

public final /* synthetic */ class AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda2 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ AudioPlayerAlert.CoverContainer f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda2(AudioPlayerAlert.CoverContainer coverContainer, int i) {
        this.f$0 = coverContainer;
        this.f$1 = i;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.m552x457889fd(this.f$1, imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

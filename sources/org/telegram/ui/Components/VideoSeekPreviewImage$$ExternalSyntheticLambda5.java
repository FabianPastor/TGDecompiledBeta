package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class VideoSeekPreviewImage$$ExternalSyntheticLambda5 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ VideoSeekPreviewImage f$0;

    public /* synthetic */ VideoSeekPreviewImage$$ExternalSyntheticLambda5(VideoSeekPreviewImage videoSeekPreviewImage) {
        this.f$0 = videoSeekPreviewImage;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.lambda$new$0(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

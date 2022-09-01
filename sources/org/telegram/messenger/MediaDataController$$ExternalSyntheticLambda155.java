package org.telegram.messenger;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda155 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ ImageReceiver f$0;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda155(ImageReceiver imageReceiver) {
        this.f$0 = imageReceiver;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        MediaDataController.lambda$preloadImage$14(this.f$0, imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

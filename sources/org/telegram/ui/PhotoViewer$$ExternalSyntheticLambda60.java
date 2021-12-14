package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda60 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda60(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.lambda$setParentActivity$32(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

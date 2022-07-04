package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda69 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda69(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.m4278lambda$setParentActivity$35$orgtelegramuiPhotoViewer(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

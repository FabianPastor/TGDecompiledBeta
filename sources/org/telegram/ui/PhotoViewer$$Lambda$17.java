package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;

final /* synthetic */ class PhotoViewer$$Lambda$17 implements ImageReceiverDelegate {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$17(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        this.arg$1.lambda$setParentActivity$22$PhotoViewer(imageReceiver, z, z2);
    }
}

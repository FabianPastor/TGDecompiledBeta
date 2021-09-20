package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class PaintingOverlay$$ExternalSyntheticLambda0 implements ImageReceiver.ImageReceiverDelegate {
    public static final /* synthetic */ PaintingOverlay$$ExternalSyntheticLambda0 INSTANCE = new PaintingOverlay$$ExternalSyntheticLambda0();

    private /* synthetic */ PaintingOverlay$$ExternalSyntheticLambda0() {
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        PaintingOverlay.lambda$setEntities$0(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;

/* renamed from: org.telegram.ui.Components.-$$Lambda$PaintingOverlay$y_8jID_HHjHQkiS8U9VB2FHG3GM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PaintingOverlay$y_8jID_HHjHQkiS8U9VB2FHG3GM implements ImageReceiver.ImageReceiverDelegate {
    public static final /* synthetic */ $$Lambda$PaintingOverlay$y_8jID_HHjHQkiS8U9VB2FHG3GM INSTANCE = new $$Lambda$PaintingOverlay$y_8jID_HHjHQkiS8U9VB2FHG3GM();

    private /* synthetic */ $$Lambda$PaintingOverlay$y_8jID_HHjHQkiS8U9VB2FHG3GM() {
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        PaintingOverlay.lambda$setEntities$0(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

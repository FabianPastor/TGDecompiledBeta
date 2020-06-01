package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;

/* renamed from: org.telegram.ui.Components.-$$Lambda$PaintingOverlay$M1M-bD6dSCe7rjuKTW4iqhjJx7E  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PaintingOverlay$M1MbD6dSCe7rjuKTW4iqhjJx7E implements ImageReceiver.ImageReceiverDelegate {
    public static final /* synthetic */ $$Lambda$PaintingOverlay$M1MbD6dSCe7rjuKTW4iqhjJx7E INSTANCE = new $$Lambda$PaintingOverlay$M1MbD6dSCe7rjuKTW4iqhjJx7E();

    private /* synthetic */ $$Lambda$PaintingOverlay$M1MbD6dSCe7rjuKTW4iqhjJx7E() {
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        PaintingOverlay.lambda$setEntities$0(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

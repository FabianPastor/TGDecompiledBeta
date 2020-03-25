package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;

/* renamed from: org.telegram.ui.Components.-$$Lambda$TextPaintImageReceiverSpan$x0NtYGCv_nR9J6999CLASSNAMEM45NipY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TextPaintImageReceiverSpan$x0NtYGCv_nR9J6999CLASSNAMEM45NipY implements ImageReceiver.ImageReceiverDelegate {
    public static final /* synthetic */ $$Lambda$TextPaintImageReceiverSpan$x0NtYGCv_nR9J6999CLASSNAMEM45NipY INSTANCE = new $$Lambda$TextPaintImageReceiverSpan$x0NtYGCv_nR9J6999CLASSNAMEM45NipY();

    private /* synthetic */ $$Lambda$TextPaintImageReceiverSpan$x0NtYGCv_nR9J6999CLASSNAMEM45NipY() {
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        TextPaintImageReceiverSpan.lambda$new$0(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

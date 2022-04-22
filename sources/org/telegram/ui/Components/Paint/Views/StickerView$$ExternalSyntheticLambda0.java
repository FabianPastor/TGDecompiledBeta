package org.telegram.ui.Components.Paint.Views;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class StickerView$$ExternalSyntheticLambda0 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ StickerView f$0;

    public /* synthetic */ StickerView$$ExternalSyntheticLambda0(StickerView stickerView) {
        this.f$0 = stickerView;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.lambda$new$0(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

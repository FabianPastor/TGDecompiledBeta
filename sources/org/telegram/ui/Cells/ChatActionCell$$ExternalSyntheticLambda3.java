package org.telegram.ui.Cells;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class ChatActionCell$$ExternalSyntheticLambda3 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ ChatActionCell f$0;

    public /* synthetic */ ChatActionCell$$ExternalSyntheticLambda3(ChatActionCell chatActionCell) {
        this.f$0 = chatActionCell;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.lambda$new$0(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

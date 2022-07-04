package org.telegram.ui.Components;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class AttachBotIntroTopView$$ExternalSyntheticLambda1 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ AttachBotIntroTopView f$0;

    public /* synthetic */ AttachBotIntroTopView$$ExternalSyntheticLambda1(AttachBotIntroTopView attachBotIntroTopView) {
        this.f$0 = attachBotIntroTopView;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.m539lambda$new$1$orgtelegramuiComponentsAttachBotIntroTopView(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class VoIPFragment$$ExternalSyntheticLambda21 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ VoIPFragment f$0;

    public /* synthetic */ VoIPFragment$$ExternalSyntheticLambda21(VoIPFragment voIPFragment) {
        this.f$0 = voIPFragment;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.m4807lambda$createView$4$orgtelegramuiVoIPFragment(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

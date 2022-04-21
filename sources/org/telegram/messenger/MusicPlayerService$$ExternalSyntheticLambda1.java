package org.telegram.messenger;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class MusicPlayerService$$ExternalSyntheticLambda1 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ MusicPlayerService f$0;

    public /* synthetic */ MusicPlayerService$$ExternalSyntheticLambda1(MusicPlayerService musicPlayerService) {
        this.f$0 = musicPlayerService;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.m1034lambda$onCreate$0$orgtelegrammessengerMusicPlayerService(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

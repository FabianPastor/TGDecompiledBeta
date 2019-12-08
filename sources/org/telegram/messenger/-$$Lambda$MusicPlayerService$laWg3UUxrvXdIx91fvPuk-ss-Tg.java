package org.telegram.messenger;

import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MusicPlayerService$laWg3UUxrvXdIx91fvPuk-ss-Tg implements ImageReceiverDelegate {
    private final /* synthetic */ MusicPlayerService f$0;

    public /* synthetic */ -$$Lambda$MusicPlayerService$laWg3UUxrvXdIx91fvPuk-ss-Tg(MusicPlayerService musicPlayerService) {
        this.f$0 = musicPlayerService;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        this.f$0.lambda$onCreate$0$MusicPlayerService(imageReceiver, z, z2);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        -CC.$default$onAnimationReady(this, imageReceiver);
    }
}

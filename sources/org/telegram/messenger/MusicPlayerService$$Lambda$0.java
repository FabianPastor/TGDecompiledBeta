package org.telegram.messenger;

import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;

final /* synthetic */ class MusicPlayerService$$Lambda$0 implements ImageReceiverDelegate {
    private final MusicPlayerService arg$1;

    MusicPlayerService$$Lambda$0(MusicPlayerService musicPlayerService) {
        this.arg$1 = musicPlayerService;
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        this.arg$1.lambda$onCreate$0$MusicPlayerService(imageReceiver, z, z2);
    }
}

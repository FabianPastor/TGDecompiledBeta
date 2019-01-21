package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;

final /* synthetic */ class WallpaperActivity$$Lambda$0 implements ImageReceiverDelegate {
    private final WallpaperActivity arg$1;

    WallpaperActivity$$Lambda$0(WallpaperActivity wallpaperActivity) {
        this.arg$1 = wallpaperActivity;
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        this.arg$1.lambda$createView$0$WallpaperActivity(imageReceiver, z, z2);
    }
}

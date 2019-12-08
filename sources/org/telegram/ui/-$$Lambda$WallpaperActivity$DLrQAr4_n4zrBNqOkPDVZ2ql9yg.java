package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WallpaperActivity$DLrQAr4_n4zrBNqOkPDVZ2ql9yg implements ImageReceiverDelegate {
    private final /* synthetic */ WallpaperActivity f$0;

    public /* synthetic */ -$$Lambda$WallpaperActivity$DLrQAr4_n4zrBNqOkPDVZ2ql9yg(WallpaperActivity wallpaperActivity) {
        this.f$0 = wallpaperActivity;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        this.f$0.lambda$createView$0$WallpaperActivity(imageReceiver, z, z2);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        -CC.$default$onAnimationReady(this, imageReceiver);
    }
}

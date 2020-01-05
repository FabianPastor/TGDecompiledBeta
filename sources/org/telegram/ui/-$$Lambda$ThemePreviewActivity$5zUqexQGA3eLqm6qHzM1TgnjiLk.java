package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate.-CC;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$5zUqexQGA3eLqm6qHzM1TgnjiLk implements ImageReceiverDelegate {
    private final /* synthetic */ ThemePreviewActivity f$0;

    public /* synthetic */ -$$Lambda$ThemePreviewActivity$5zUqexQGA3eLqm6qHzM1TgnjiLk(ThemePreviewActivity themePreviewActivity) {
        this.f$0 = themePreviewActivity;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
        this.f$0.lambda$createView$1$ThemePreviewActivity(imageReceiver, z, z2);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        -CC.$default$onAnimationReady(this, imageReceiver);
    }
}

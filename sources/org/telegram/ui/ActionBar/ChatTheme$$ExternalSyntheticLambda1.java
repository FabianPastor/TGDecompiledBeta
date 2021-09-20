package org.telegram.ui.ActionBar;

import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.ResultCallback;

public final /* synthetic */ class ChatTheme$$ExternalSyntheticLambda1 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ChatTheme$$ExternalSyntheticLambda1(ResultCallback resultCallback, long j) {
        this.f$0 = resultCallback;
        this.f$1 = j;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ChatTheme.lambda$loadWallpaper$0(this.f$0, this.f$1, imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

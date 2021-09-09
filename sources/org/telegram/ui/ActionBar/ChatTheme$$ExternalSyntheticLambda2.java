package org.telegram.ui.ActionBar;

import java.io.File;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.ResultCallback;

public final /* synthetic */ class ChatTheme$$ExternalSyntheticLambda2 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ ResultCallback f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ File f$2;

    public /* synthetic */ ChatTheme$$ExternalSyntheticLambda2(ResultCallback resultCallback, long j, File file) {
        this.f$0 = resultCallback;
        this.f$1 = j;
        this.f$2 = file;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        ChatTheme.lambda$loadWallpaperThumb$2(this.f$0, this.f$1, this.f$2, imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

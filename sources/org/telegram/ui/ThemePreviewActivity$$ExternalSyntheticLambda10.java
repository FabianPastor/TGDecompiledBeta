package org.telegram.ui;

import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class ThemePreviewActivity$$ExternalSyntheticLambda10 implements ImageReceiver.ImageReceiverDelegate {
    public final /* synthetic */ ThemePreviewActivity f$0;

    public /* synthetic */ ThemePreviewActivity$$ExternalSyntheticLambda10(ThemePreviewActivity themePreviewActivity) {
        this.f$0 = themePreviewActivity;
    }

    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        this.f$0.m3932lambda$createView$2$orgtelegramuiThemePreviewActivity(imageReceiver, z, z2, z3);
    }

    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
    }
}

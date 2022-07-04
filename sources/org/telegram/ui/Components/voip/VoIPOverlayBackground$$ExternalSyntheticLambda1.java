package org.telegram.ui.Components.voip;

import android.graphics.Bitmap;
import org.telegram.messenger.ImageReceiver;

public final /* synthetic */ class VoIPOverlayBackground$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ VoIPOverlayBackground f$0;
    public final /* synthetic */ Bitmap f$1;
    public final /* synthetic */ ImageReceiver.BitmapHolder f$2;

    public /* synthetic */ VoIPOverlayBackground$$ExternalSyntheticLambda1(VoIPOverlayBackground voIPOverlayBackground, Bitmap bitmap, ImageReceiver.BitmapHolder bitmapHolder) {
        this.f$0 = voIPOverlayBackground;
        this.f$1 = bitmap;
        this.f$2 = bitmapHolder;
    }

    public final void run() {
        this.f$0.m1612x9c3bd26d(this.f$1, this.f$2);
    }
}

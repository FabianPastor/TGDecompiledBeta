package org.telegram.ui.Components;

import android.graphics.Bitmap;

public final /* synthetic */ class VideoSeekPreviewImage$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ VideoSeekPreviewImage f$0;
    public final /* synthetic */ Bitmap f$1;

    public /* synthetic */ VideoSeekPreviewImage$$ExternalSyntheticLambda3(VideoSeekPreviewImage videoSeekPreviewImage, Bitmap bitmap) {
        this.f$0 = videoSeekPreviewImage;
        this.f$1 = bitmap;
    }

    public final void run() {
        this.f$0.lambda$setProgress$1(this.f$1);
    }
}

package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import org.telegram.messenger.ImageLoader;
import org.telegram.ui.Components.RLottieDrawable;

public final /* synthetic */ class ImageLoader$CacheOutTask$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ImageLoader.CacheOutTask f$0;
    public final /* synthetic */ RLottieDrawable f$1;
    public final /* synthetic */ Canvas f$2;
    public final /* synthetic */ Bitmap f$3;

    public /* synthetic */ ImageLoader$CacheOutTask$$ExternalSyntheticLambda2(ImageLoader.CacheOutTask cacheOutTask, RLottieDrawable rLottieDrawable, Canvas canvas, Bitmap bitmap) {
        this.f$0 = cacheOutTask;
        this.f$1 = rLottieDrawable;
        this.f$2 = canvas;
        this.f$3 = bitmap;
    }

    public final void run() {
        this.f$0.lambda$loadLastFrame$0(this.f$1, this.f$2, this.f$3);
    }
}

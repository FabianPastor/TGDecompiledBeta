package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import org.telegram.messenger.ImageLoader;
import org.telegram.ui.Components.RLottieDrawable;

public final /* synthetic */ class ImageLoader$CacheOutTask$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ImageLoader.CacheOutTask f$0;
    public final /* synthetic */ RLottieDrawable f$1;
    public final /* synthetic */ Canvas f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ Bitmap f$6;

    public /* synthetic */ ImageLoader$CacheOutTask$$ExternalSyntheticLambda3(ImageLoader.CacheOutTask cacheOutTask, RLottieDrawable rLottieDrawable, Canvas canvas, boolean z, int i, int i2, Bitmap bitmap) {
        this.f$0 = cacheOutTask;
        this.f$1 = rLottieDrawable;
        this.f$2 = canvas;
        this.f$3 = z;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = bitmap;
    }

    public final void run() {
        this.f$0.lambda$loadLastFrame$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

package org.telegram.messenger;

import org.telegram.ui.Components.RLottieDrawable;

public final /* synthetic */ class ImageLoader$CacheOutTask$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ RLottieDrawable f$0;

    public /* synthetic */ ImageLoader$CacheOutTask$$ExternalSyntheticLambda2(RLottieDrawable rLottieDrawable) {
        this.f$0 = rLottieDrawable;
    }

    public final void run() {
        this.f$0.recycle();
    }
}

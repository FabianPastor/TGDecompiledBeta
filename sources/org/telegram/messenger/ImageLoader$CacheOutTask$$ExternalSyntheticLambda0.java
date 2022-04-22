package org.telegram.messenger;

import android.graphics.drawable.Drawable;
import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$CacheOutTask$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ImageLoader.CacheOutTask f$0;
    public final /* synthetic */ Drawable f$1;

    public /* synthetic */ ImageLoader$CacheOutTask$$ExternalSyntheticLambda0(ImageLoader.CacheOutTask cacheOutTask, Drawable drawable) {
        this.f$0 = cacheOutTask;
        this.f$1 = drawable;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$3(this.f$1);
    }
}

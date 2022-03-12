package org.telegram.messenger;

import android.graphics.drawable.Drawable;
import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$CacheOutTask$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ImageLoader.CacheOutTask f$0;
    public final /* synthetic */ Drawable f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ ImageLoader$CacheOutTask$$ExternalSyntheticLambda1(ImageLoader.CacheOutTask cacheOutTask, Drawable drawable, String str) {
        this.f$0 = cacheOutTask;
        this.f$1 = drawable;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$2(this.f$1, this.f$2);
    }
}

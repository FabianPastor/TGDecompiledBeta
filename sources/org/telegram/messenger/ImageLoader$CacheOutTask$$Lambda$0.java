package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;

final /* synthetic */ class ImageLoader$CacheOutTask$$Lambda$0 implements Runnable {
    private final CacheOutTask arg$1;
    private final BitmapDrawable arg$2;

    ImageLoader$CacheOutTask$$Lambda$0(CacheOutTask cacheOutTask, BitmapDrawable bitmapDrawable) {
        this.arg$1 = cacheOutTask;
        this.arg$2 = bitmapDrawable;
    }

    public void run() {
        this.arg$1.lambda$onPostExecute$1$ImageLoader$CacheOutTask(this.arg$2);
    }
}

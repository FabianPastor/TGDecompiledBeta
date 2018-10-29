package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;

final /* synthetic */ class ImageLoader$CacheImage$$Lambda$0 implements Runnable {
    private final CacheImage arg$1;
    private final BitmapDrawable arg$2;
    private final ArrayList arg$3;

    ImageLoader$CacheImage$$Lambda$0(CacheImage cacheImage, BitmapDrawable bitmapDrawable, ArrayList arrayList) {
        this.arg$1 = cacheImage;
        this.arg$2 = bitmapDrawable;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$setImageAndClear$0$ImageLoader$CacheImage(this.arg$2, this.arg$3);
    }
}

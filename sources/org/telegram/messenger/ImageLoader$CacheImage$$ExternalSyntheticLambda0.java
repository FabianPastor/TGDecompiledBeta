package org.telegram.messenger;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$CacheImage$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ImageLoader.CacheImage f$0;
    public final /* synthetic */ Drawable f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ ImageLoader$CacheImage$$ExternalSyntheticLambda0(ImageLoader.CacheImage cacheImage, Drawable drawable, ArrayList arrayList, ArrayList arrayList2, String str) {
        this.f$0 = cacheImage;
        this.f$1 = drawable;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.m628x9483CLASSNAME(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

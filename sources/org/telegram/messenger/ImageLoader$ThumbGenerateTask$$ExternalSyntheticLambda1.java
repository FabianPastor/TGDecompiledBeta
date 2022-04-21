package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;
import org.telegram.messenger.ImageLoader;

public final /* synthetic */ class ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ImageLoader.ThumbGenerateTask f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ BitmapDrawable f$3;
    public final /* synthetic */ ArrayList f$4;

    public /* synthetic */ ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda1(ImageLoader.ThumbGenerateTask thumbGenerateTask, String str, ArrayList arrayList, BitmapDrawable bitmapDrawable, ArrayList arrayList2) {
        this.f$0 = thumbGenerateTask;
        this.f$1 = str;
        this.f$2 = arrayList;
        this.f$3 = bitmapDrawable;
        this.f$4 = arrayList2;
    }

    public final void run() {
        this.f$0.m644x4b36var_(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;

final /* synthetic */ class ImageLoader$ThumbGenerateTask$$Lambda$1 implements Runnable {
    private final ThumbGenerateTask arg$1;
    private final String arg$2;
    private final ArrayList arg$3;
    private final BitmapDrawable arg$4;

    ImageLoader$ThumbGenerateTask$$Lambda$1(ThumbGenerateTask thumbGenerateTask, String str, ArrayList arrayList, BitmapDrawable bitmapDrawable) {
        this.arg$1 = thumbGenerateTask;
        this.arg$2 = str;
        this.arg$3 = arrayList;
        this.arg$4 = bitmapDrawable;
    }

    public void run() {
        this.arg$1.lambda$run$1$ImageLoader$ThumbGenerateTask(this.arg$2, this.arg$3, this.arg$4);
    }
}

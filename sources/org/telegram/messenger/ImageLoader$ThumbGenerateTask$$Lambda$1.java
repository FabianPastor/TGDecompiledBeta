package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;

final /* synthetic */ class ImageLoader$ThumbGenerateTask$$Lambda$1 implements Runnable {
    private final ThumbGenerateTask arg$1;
    private final String arg$2;
    private final BitmapDrawable arg$3;

    ImageLoader$ThumbGenerateTask$$Lambda$1(ThumbGenerateTask thumbGenerateTask, String str, BitmapDrawable bitmapDrawable) {
        this.arg$1 = thumbGenerateTask;
        this.arg$2 = str;
        this.arg$3 = bitmapDrawable;
    }

    public void run() {
        this.arg$1.lambda$run$1$ImageLoader$ThumbGenerateTask(this.arg$2, this.arg$3);
    }
}

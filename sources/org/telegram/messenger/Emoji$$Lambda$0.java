package org.telegram.messenger;

import android.graphics.Bitmap;

final /* synthetic */ class Emoji$$Lambda$0 implements Runnable {
    private final int arg$1;
    private final int arg$2;
    private final Bitmap arg$3;

    Emoji$$Lambda$0(int i, int i2, Bitmap bitmap) {
        this.arg$1 = i;
        this.arg$2 = i2;
        this.arg$3 = bitmap;
    }

    public void run() {
        Emoji.lambda$loadEmoji$0$Emoji(this.arg$1, this.arg$2, this.arg$3);
    }
}

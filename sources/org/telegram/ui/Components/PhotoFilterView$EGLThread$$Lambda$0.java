package org.telegram.ui.Components;

import android.graphics.Bitmap;
import java.util.concurrent.CountDownLatch;
import org.telegram.ui.Components.PhotoFilterView.EGLThread;

final /* synthetic */ class PhotoFilterView$EGLThread$$Lambda$0 implements Runnable {
    private final EGLThread arg$1;
    private final Bitmap[] arg$2;
    private final CountDownLatch arg$3;

    PhotoFilterView$EGLThread$$Lambda$0(EGLThread eGLThread, Bitmap[] bitmapArr, CountDownLatch countDownLatch) {
        this.arg$1 = eGLThread;
        this.arg$2 = bitmapArr;
        this.arg$3 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getTexture$0$PhotoFilterView$EGLThread(this.arg$2, this.arg$3);
    }
}

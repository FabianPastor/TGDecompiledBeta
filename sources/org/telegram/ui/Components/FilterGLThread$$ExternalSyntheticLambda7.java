package org.telegram.ui.Components;

import android.graphics.Bitmap;
import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class FilterGLThread$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ FilterGLThread f$0;
    public final /* synthetic */ Bitmap[] f$1;
    public final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ FilterGLThread$$ExternalSyntheticLambda7(FilterGLThread filterGLThread, Bitmap[] bitmapArr, CountDownLatch countDownLatch) {
        this.f$0 = filterGLThread;
        this.f$1 = bitmapArr;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getTexture$4(this.f$1, this.f$2);
    }
}

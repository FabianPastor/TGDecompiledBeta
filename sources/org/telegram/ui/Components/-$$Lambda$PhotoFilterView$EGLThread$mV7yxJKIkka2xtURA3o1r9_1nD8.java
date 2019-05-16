package org.telegram.ui.Components;

import android.graphics.Bitmap;
import java.util.concurrent.CountDownLatch;
import org.telegram.ui.Components.PhotoFilterView.EGLThread;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoFilterView$EGLThread$mV7yxJKIkka2xtURA3o1r9_1nD8 implements Runnable {
    private final /* synthetic */ EGLThread f$0;
    private final /* synthetic */ Bitmap[] f$1;
    private final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ -$$Lambda$PhotoFilterView$EGLThread$mV7yxJKIkka2xtURA3o1r9_1nD8(EGLThread eGLThread, Bitmap[] bitmapArr, CountDownLatch countDownLatch) {
        this.f$0 = eGLThread;
        this.f$1 = bitmapArr;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getTexture$0$PhotoFilterView$EGLThread(this.f$1, this.f$2);
    }
}

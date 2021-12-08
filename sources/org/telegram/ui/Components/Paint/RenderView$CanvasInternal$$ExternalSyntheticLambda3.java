package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import java.util.concurrent.CountDownLatch;
import org.telegram.ui.Components.Paint.RenderView;

public final /* synthetic */ class RenderView$CanvasInternal$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ RenderView.CanvasInternal f$0;
    public final /* synthetic */ Bitmap[] f$1;
    public final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ RenderView$CanvasInternal$$ExternalSyntheticLambda3(RenderView.CanvasInternal canvasInternal, Bitmap[] bitmapArr, CountDownLatch countDownLatch) {
        this.f$0 = canvasInternal;
        this.f$1 = bitmapArr;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        this.f$0.m2426x889307e1(this.f$1, this.f$2);
    }
}

package org.telegram.ui.Components;

import android.graphics.Bitmap;
import org.telegram.ui.Components.BackgroundGradientDrawable;

public final /* synthetic */ class BackgroundGradientDrawable$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ BackgroundGradientDrawable f$0;
    public final /* synthetic */ Runnable[] f$1;
    public final /* synthetic */ Bitmap f$2;
    public final /* synthetic */ IntSize f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ BackgroundGradientDrawable.Listener[] f$5;

    public /* synthetic */ BackgroundGradientDrawable$$ExternalSyntheticLambda1(BackgroundGradientDrawable backgroundGradientDrawable, Runnable[] runnableArr, Bitmap bitmap, IntSize intSize, int i, BackgroundGradientDrawable.Listener[] listenerArr) {
        this.f$0 = backgroundGradientDrawable;
        this.f$1 = runnableArr;
        this.f$2 = bitmap;
        this.f$3 = intSize;
        this.f$4 = i;
        this.f$5 = listenerArr;
    }

    public final void run() {
        this.f$0.lambda$startDitheringInternal$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

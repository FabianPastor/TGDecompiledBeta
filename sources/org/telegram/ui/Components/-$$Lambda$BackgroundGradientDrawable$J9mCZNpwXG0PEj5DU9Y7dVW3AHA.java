package org.telegram.ui.Components;

import android.graphics.Bitmap;
import org.telegram.ui.Components.BackgroundGradientDrawable.Listener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BackgroundGradientDrawable$J9mCZNpwXG0PEj5DU9Y7dVW3AHA implements Runnable {
    private final /* synthetic */ BackgroundGradientDrawable f$0;
    private final /* synthetic */ Runnable[] f$1;
    private final /* synthetic */ Bitmap f$2;
    private final /* synthetic */ IntSize f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ Listener[] f$5;

    public /* synthetic */ -$$Lambda$BackgroundGradientDrawable$J9mCZNpwXG0PEj5DU9Y7dVW3AHA(BackgroundGradientDrawable backgroundGradientDrawable, Runnable[] runnableArr, Bitmap bitmap, IntSize intSize, int i, Listener[] listenerArr) {
        this.f$0 = backgroundGradientDrawable;
        this.f$1 = runnableArr;
        this.f$2 = bitmap;
        this.f$3 = intSize;
        this.f$4 = i;
        this.f$5 = listenerArr;
    }

    public final void run() {
        this.f$0.lambda$null$1$BackgroundGradientDrawable(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

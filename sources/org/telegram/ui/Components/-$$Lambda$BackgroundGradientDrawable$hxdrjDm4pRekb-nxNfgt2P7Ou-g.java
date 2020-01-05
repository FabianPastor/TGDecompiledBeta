package org.telegram.ui.Components;

import org.telegram.ui.Components.BackgroundGradientDrawable.Listener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BackgroundGradientDrawable$hxdrjDm4pRekb-nxNfgt2P7Ou-g implements Runnable {
    private final /* synthetic */ BackgroundGradientDrawable f$0;
    private final /* synthetic */ IntSize f$1;
    private final /* synthetic */ Runnable[] f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ Listener[] f$4;

    public /* synthetic */ -$$Lambda$BackgroundGradientDrawable$hxdrjDm4pRekb-nxNfgt2P7Ou-g(BackgroundGradientDrawable backgroundGradientDrawable, IntSize intSize, Runnable[] runnableArr, int i, Listener[] listenerArr) {
        this.f$0 = backgroundGradientDrawable;
        this.f$1 = intSize;
        this.f$2 = runnableArr;
        this.f$3 = i;
        this.f$4 = listenerArr;
    }

    public final void run() {
        this.f$0.lambda$startDitheringInternal$2$BackgroundGradientDrawable(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

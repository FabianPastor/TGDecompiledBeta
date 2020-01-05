package org.telegram.ui.Components;

import org.telegram.ui.Components.BackgroundGradientDrawable.Disposable;
import org.telegram.ui.Components.BackgroundGradientDrawable.Listener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BackgroundGradientDrawable$A-pstgPecKYALRS-Lp4n1_gbMG4 implements Disposable {
    private final /* synthetic */ BackgroundGradientDrawable f$0;
    private final /* synthetic */ Listener[] f$1;
    private final /* synthetic */ Runnable[] f$2;
    private final /* synthetic */ IntSize[] f$3;

    public /* synthetic */ -$$Lambda$BackgroundGradientDrawable$A-pstgPecKYALRS-Lp4n1_gbMG4(BackgroundGradientDrawable backgroundGradientDrawable, Listener[] listenerArr, Runnable[] runnableArr, IntSize[] intSizeArr) {
        this.f$0 = backgroundGradientDrawable;
        this.f$1 = listenerArr;
        this.f$2 = runnableArr;
        this.f$3 = intSizeArr;
    }

    public final void dispose() {
        this.f$0.lambda$startDitheringInternal$3$BackgroundGradientDrawable(this.f$1, this.f$2, this.f$3);
    }
}

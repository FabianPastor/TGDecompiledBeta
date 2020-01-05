package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.BackgroundGradientDrawable.Disposable;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BackgroundGradientDrawable$GzEdPAGaK9eizgcofyUmITEmyAs implements Disposable {
    private final /* synthetic */ BackgroundGradientDrawable f$0;
    private final /* synthetic */ View f$1;
    private final /* synthetic */ Disposable f$2;

    public /* synthetic */ -$$Lambda$BackgroundGradientDrawable$GzEdPAGaK9eizgcofyUmITEmyAs(BackgroundGradientDrawable backgroundGradientDrawable, View view, Disposable disposable) {
        this.f$0 = backgroundGradientDrawable;
        this.f$1 = view;
        this.f$2 = disposable;
    }

    public final void dispose() {
        this.f$0.lambda$drawExactBoundsSize$0$BackgroundGradientDrawable(this.f$1, this.f$2);
    }
}

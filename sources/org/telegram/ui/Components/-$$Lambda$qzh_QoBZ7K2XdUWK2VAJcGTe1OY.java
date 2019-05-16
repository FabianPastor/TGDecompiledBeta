package org.telegram.ui.Components;

import android.view.ViewTreeObserver.OnPreDrawListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$qzh_QoBZ7K2XdUWK2VAJcGTe1OY implements Runnable {
    private final /* synthetic */ OnPreDrawListener f$0;

    public /* synthetic */ -$$Lambda$qzh_QoBZ7K2XdUWK2VAJcGTe1OY(OnPreDrawListener onPreDrawListener) {
        this.f$0 = onPreDrawListener;
    }

    public final void run() {
        this.f$0.onPreDraw();
    }
}

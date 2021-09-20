package org.telegram.ui.Components;

import android.view.ViewTreeObserver;

public final /* synthetic */ class EditTextBoldCursor$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ViewTreeObserver.OnPreDrawListener f$0;

    public /* synthetic */ EditTextBoldCursor$$ExternalSyntheticLambda1(ViewTreeObserver.OnPreDrawListener onPreDrawListener) {
        this.f$0 = onPreDrawListener;
    }

    public final void run() {
        this.f$0.onPreDraw();
    }
}

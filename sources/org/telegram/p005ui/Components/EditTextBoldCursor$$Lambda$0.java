package org.telegram.p005ui.Components;

import android.view.ViewTreeObserver.OnPreDrawListener;

/* renamed from: org.telegram.ui.Components.EditTextBoldCursor$$Lambda$0 */
final /* synthetic */ class EditTextBoldCursor$$Lambda$0 implements Runnable {
    private final OnPreDrawListener arg$1;

    private EditTextBoldCursor$$Lambda$0(OnPreDrawListener onPreDrawListener) {
        this.arg$1 = onPreDrawListener;
    }

    static Runnable get$Lambda(OnPreDrawListener onPreDrawListener) {
        return new EditTextBoldCursor$$Lambda$0(onPreDrawListener);
    }

    public void run() {
        this.arg$1.onPreDraw();
    }
}

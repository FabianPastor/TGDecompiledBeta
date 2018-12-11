package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.PhotoFilterView.EGLThread;

/* renamed from: org.telegram.ui.Components.PhotoFilterView$EGLThread$$Lambda$1 */
final /* synthetic */ class PhotoFilterView$EGLThread$$Lambda$1 implements Runnable {
    private final EGLThread arg$1;

    PhotoFilterView$EGLThread$$Lambda$1(EGLThread eGLThread) {
        this.arg$1 = eGLThread;
    }

    public void run() {
        this.arg$1.lambda$shutdown$1$PhotoFilterView$EGLThread();
    }
}

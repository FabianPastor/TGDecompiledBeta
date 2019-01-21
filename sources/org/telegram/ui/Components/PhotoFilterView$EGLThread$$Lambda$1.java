package org.telegram.ui.Components;

import org.telegram.ui.Components.PhotoFilterView.EGLThread;

final /* synthetic */ class PhotoFilterView$EGLThread$$Lambda$1 implements Runnable {
    private final EGLThread arg$1;

    PhotoFilterView$EGLThread$$Lambda$1(EGLThread eGLThread) {
        this.arg$1 = eGLThread;
    }

    public void run() {
        this.arg$1.lambda$shutdown$1$PhotoFilterView$EGLThread();
    }
}

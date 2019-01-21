package org.telegram.ui.Components;

import org.telegram.ui.Components.PhotoFilterView.EGLThread;

final /* synthetic */ class PhotoFilterView$EGLThread$$Lambda$2 implements Runnable {
    private final EGLThread arg$1;
    private final boolean arg$2;
    private final boolean arg$3;

    PhotoFilterView$EGLThread$$Lambda$2(EGLThread eGLThread, boolean z, boolean z2) {
        this.arg$1 = eGLThread;
        this.arg$2 = z;
        this.arg$3 = z2;
    }

    public void run() {
        this.arg$1.lambda$requestRender$2$PhotoFilterView$EGLThread(this.arg$2, this.arg$3);
    }
}

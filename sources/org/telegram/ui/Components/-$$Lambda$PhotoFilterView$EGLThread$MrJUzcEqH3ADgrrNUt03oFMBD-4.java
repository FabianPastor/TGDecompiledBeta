package org.telegram.ui.Components;

import org.telegram.ui.Components.PhotoFilterView.EGLThread;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoFilterView$EGLThread$MrJUzcEqH3ADgrrNUt03oFMBD-4 implements Runnable {
    private final /* synthetic */ EGLThread f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$PhotoFilterView$EGLThread$MrJUzcEqH3ADgrrNUt03oFMBD-4(EGLThread eGLThread, boolean z, boolean z2) {
        this.f$0 = eGLThread;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void run() {
        this.f$0.lambda$requestRender$2$PhotoFilterView$EGLThread(this.f$1, this.f$2);
    }
}

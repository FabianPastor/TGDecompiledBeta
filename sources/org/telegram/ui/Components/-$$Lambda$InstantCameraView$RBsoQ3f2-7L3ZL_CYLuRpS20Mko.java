package org.telegram.ui.Components;

import android.graphics.SurfaceTexture;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$InstantCameraView$RBsoQ3f2-7L3ZL_CYLuRpS20Mko implements Runnable {
    private final /* synthetic */ InstantCameraView f$0;
    private final /* synthetic */ SurfaceTexture f$1;

    public /* synthetic */ -$$Lambda$InstantCameraView$RBsoQ3f2-7L3ZL_CYLuRpS20Mko(InstantCameraView instantCameraView, SurfaceTexture surfaceTexture) {
        this.f$0 = instantCameraView;
        this.f$1 = surfaceTexture;
    }

    public final void run() {
        this.f$0.lambda$createCamera$4$InstantCameraView(this.f$1);
    }
}

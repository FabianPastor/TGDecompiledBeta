package org.telegram.p005ui.Components;

import android.graphics.SurfaceTexture;

/* renamed from: org.telegram.ui.Components.InstantCameraView$$Lambda$2 */
final /* synthetic */ class InstantCameraView$$Lambda$2 implements Runnable {
    private final InstantCameraView arg$1;
    private final SurfaceTexture arg$2;

    InstantCameraView$$Lambda$2(InstantCameraView instantCameraView, SurfaceTexture surfaceTexture) {
        this.arg$1 = instantCameraView;
        this.arg$2 = surfaceTexture;
    }

    public void run() {
        this.arg$1.lambda$createCamera$4$InstantCameraView(this.arg$2);
    }
}

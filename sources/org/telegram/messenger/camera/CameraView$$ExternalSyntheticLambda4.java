package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

public final /* synthetic */ class CameraView$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ CameraView f$0;
    public final /* synthetic */ SurfaceTexture f$1;

    public /* synthetic */ CameraView$$ExternalSyntheticLambda4(CameraView cameraView, SurfaceTexture surfaceTexture) {
        this.f$0 = cameraView;
        this.f$1 = surfaceTexture;
    }

    public final void run() {
        this.f$0.lambda$createCamera$4(this.f$1);
    }
}
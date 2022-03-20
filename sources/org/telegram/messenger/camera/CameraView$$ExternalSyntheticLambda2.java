package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

public final /* synthetic */ class CameraView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ CameraView f$0;
    public final /* synthetic */ SurfaceTexture f$1;

    public /* synthetic */ CameraView$$ExternalSyntheticLambda2(CameraView cameraView, SurfaceTexture surfaceTexture) {
        this.f$0 = cameraView;
        this.f$1 = surfaceTexture;
    }

    public final void run() {
        this.f$0.lambda$createCamera$2(this.f$1);
    }
}

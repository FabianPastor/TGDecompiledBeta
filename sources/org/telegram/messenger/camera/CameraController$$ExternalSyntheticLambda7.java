package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ CameraSession f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ SurfaceTexture f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda7(CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        this.f$0 = cameraSession;
        this.f$1 = runnable;
        this.f$2 = surfaceTexture;
        this.f$3 = runnable2;
    }

    public final void run() {
        CameraController.lambda$openRound$9(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}

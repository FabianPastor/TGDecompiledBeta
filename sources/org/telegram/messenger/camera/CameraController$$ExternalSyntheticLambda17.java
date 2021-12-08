package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ CameraController f$0;
    public final /* synthetic */ CameraSession f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ SurfaceTexture f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda17(CameraController cameraController, CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        this.f$0 = cameraController;
        this.f$1 = cameraSession;
        this.f$2 = runnable;
        this.f$3 = surfaceTexture;
        this.f$4 = runnable2;
    }

    public final void run() {
        this.f$0.m1174lambda$open$10$orgtelegrammessengercameraCameraController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

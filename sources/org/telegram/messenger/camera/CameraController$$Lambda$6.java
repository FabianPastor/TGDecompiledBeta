package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

final /* synthetic */ class CameraController$$Lambda$6 implements Runnable {
    private final CameraController arg$1;
    private final CameraSession arg$2;
    private final Runnable arg$3;
    private final SurfaceTexture arg$4;
    private final Runnable arg$5;

    CameraController$$Lambda$6(CameraController cameraController, CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        this.arg$1 = cameraController;
        this.arg$2 = cameraSession;
        this.arg$3 = runnable;
        this.arg$4 = surfaceTexture;
        this.arg$5 = runnable2;
    }

    public void run() {
        this.arg$1.lambda$open$9$CameraController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

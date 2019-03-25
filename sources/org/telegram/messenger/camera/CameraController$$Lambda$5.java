package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

final /* synthetic */ class CameraController$$Lambda$5 implements Runnable {
    private final CameraSession arg$1;
    private final Runnable arg$2;
    private final SurfaceTexture arg$3;
    private final Runnable arg$4;

    CameraController$$Lambda$5(CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        this.arg$1 = cameraSession;
        this.arg$2 = runnable;
        this.arg$3 = surfaceTexture;
        this.arg$4 = runnable2;
    }

    public void run() {
        CameraController.lambda$openRound$8$CameraController(this.arg$1, this.arg$2, this.arg$3, this.arg$4);
    }
}

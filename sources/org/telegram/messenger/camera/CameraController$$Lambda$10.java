package org.telegram.messenger.camera;

import android.hardware.Camera;

final /* synthetic */ class CameraController$$Lambda$10 implements Runnable {
    private final Camera arg$1;
    private final CameraSession arg$2;

    CameraController$$Lambda$10(Camera camera, CameraSession cameraSession) {
        this.arg$1 = camera;
        this.arg$2 = cameraSession;
    }

    public void run() {
        CameraController.lambda$null$12$CameraController(this.arg$1, this.arg$2);
    }
}

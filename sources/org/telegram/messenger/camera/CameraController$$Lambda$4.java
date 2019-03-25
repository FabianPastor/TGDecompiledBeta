package org.telegram.messenger.camera;

final /* synthetic */ class CameraController$$Lambda$4 implements Runnable {
    private final CameraSession arg$1;

    CameraController$$Lambda$4(CameraSession cameraSession) {
        this.arg$1 = cameraSession;
    }

    public void run() {
        CameraController.lambda$stopPreview$7$CameraController(this.arg$1);
    }
}

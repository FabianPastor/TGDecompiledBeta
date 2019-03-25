package org.telegram.messenger.camera;

final /* synthetic */ class CameraController$$Lambda$3 implements Runnable {
    private final CameraSession arg$1;

    CameraController$$Lambda$3(CameraSession cameraSession) {
        this.arg$1 = cameraSession;
    }

    public void run() {
        CameraController.lambda$startPreview$6$CameraController(this.arg$1);
    }
}

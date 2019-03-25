package org.telegram.messenger.camera;

final /* synthetic */ class CameraController$$Lambda$9 implements Runnable {
    private final CameraController arg$1;
    private final CameraSession arg$2;
    private final boolean arg$3;

    CameraController$$Lambda$9(CameraController cameraController, CameraSession cameraSession, boolean z) {
        this.arg$1 = cameraController;
        this.arg$2 = cameraSession;
        this.arg$3 = z;
    }

    public void run() {
        this.arg$1.lambda$stopVideoRecording$13$CameraController(this.arg$2, this.arg$3);
    }
}

package org.telegram.messenger.camera;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ CameraController f$0;
    public final /* synthetic */ CameraSession f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda10(CameraController cameraController, CameraSession cameraSession, boolean z) {
        this.f$0 = cameraController;
        this.f$1 = cameraSession;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$stopVideoRecording$16(this.f$1, this.f$2);
    }
}

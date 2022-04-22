package org.telegram.messenger.camera;

import android.hardware.Camera;
import java.io.File;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ CameraController f$0;
    public final /* synthetic */ Camera f$1;
    public final /* synthetic */ CameraSession f$2;
    public final /* synthetic */ CameraView f$3;
    public final /* synthetic */ File f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda5(CameraController cameraController, Camera camera, CameraSession cameraSession, CameraView cameraView, File file, Runnable runnable) {
        this.f$0 = cameraController;
        this.f$1 = camera;
        this.f$2 = cameraSession;
        this.f$3 = cameraView;
        this.f$4 = file;
        this.f$5 = runnable;
    }

    public final void run() {
        this.f$0.lambda$recordVideo$12(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

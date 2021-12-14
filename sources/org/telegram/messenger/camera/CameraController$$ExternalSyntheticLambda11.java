package org.telegram.messenger.camera;

import java.io.File;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ CameraController f$0;
    public final /* synthetic */ CameraView f$1;
    public final /* synthetic */ File f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda11(CameraController cameraController, CameraView cameraView, File file, Runnable runnable) {
        this.f$0 = cameraController;
        this.f$1 = cameraView;
        this.f$2 = file;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$recordVideo$11(this.f$1, this.f$2, this.f$3);
    }
}

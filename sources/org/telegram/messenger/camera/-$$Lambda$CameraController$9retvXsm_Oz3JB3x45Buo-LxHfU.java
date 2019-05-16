package org.telegram.messenger.camera;

import android.hardware.Camera;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$9retvXsm_Oz3JB3x45Buo-LxHfU implements Runnable {
    private final /* synthetic */ Camera f$0;
    private final /* synthetic */ CameraSession f$1;

    public /* synthetic */ -$$Lambda$CameraController$9retvXsm_Oz3JB3x45Buo-LxHfU(Camera camera, CameraSession cameraSession) {
        this.f$0 = camera;
        this.f$1 = cameraSession;
    }

    public final void run() {
        CameraController.lambda$null$12(this.f$0, this.f$1);
    }
}

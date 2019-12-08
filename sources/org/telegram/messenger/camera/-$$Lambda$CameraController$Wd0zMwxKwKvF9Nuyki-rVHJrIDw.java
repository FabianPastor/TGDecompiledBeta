package org.telegram.messenger.camera;

import android.hardware.Camera;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$Wd0zMwxKwKvF9Nuyki-rVHJrIDw implements Runnable {
    private final /* synthetic */ Camera f$0;
    private final /* synthetic */ CameraSession f$1;

    public /* synthetic */ -$$Lambda$CameraController$Wd0zMwxKwKvF9Nuyki-rVHJrIDw(Camera camera, CameraSession cameraSession) {
        this.f$0 = camera;
        this.f$1 = cameraSession;
    }

    public final void run() {
        CameraController.lambda$null$13(this.f$0, this.f$1);
    }
}

package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$SyY9K-zIbzI3Sk7osY9JY9OVXaQ implements Runnable {
    private final /* synthetic */ CameraController f$0;
    private final /* synthetic */ CameraSession f$1;
    private final /* synthetic */ Runnable f$2;
    private final /* synthetic */ SurfaceTexture f$3;
    private final /* synthetic */ Runnable f$4;

    public /* synthetic */ -$$Lambda$CameraController$SyY9K-zIbzI3Sk7osY9JY9OVXaQ(CameraController cameraController, CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        this.f$0 = cameraController;
        this.f$1 = cameraSession;
        this.f$2 = runnable;
        this.f$3 = surfaceTexture;
        this.f$4 = runnable2;
    }

    public final void run() {
        this.f$0.lambda$open$10$CameraController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$ANuBkffO3J8bla21iFygwDfs5Ss implements Runnable {
    private final /* synthetic */ CameraSession f$0;
    private final /* synthetic */ Runnable f$1;
    private final /* synthetic */ SurfaceTexture f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$CameraController$ANuBkffO3J8bla21iFygwDfs5Ss(CameraSession cameraSession, Runnable runnable, SurfaceTexture surfaceTexture, Runnable runnable2) {
        this.f$0 = cameraSession;
        this.f$1 = runnable;
        this.f$2 = surfaceTexture;
        this.f$3 = runnable2;
    }

    public final void run() {
        CameraController.lambda$openRound$8(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}

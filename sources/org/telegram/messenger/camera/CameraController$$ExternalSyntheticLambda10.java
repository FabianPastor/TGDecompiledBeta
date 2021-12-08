package org.telegram.messenger.camera;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ Runnable f$0;
    public final /* synthetic */ CameraSession f$1;
    public final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda10(Runnable runnable, CameraSession cameraSession, CountDownLatch countDownLatch) {
        this.f$0 = runnable;
        this.f$1 = cameraSession;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        CameraController.lambda$close$5(this.f$0, this.f$1, this.f$2);
    }
}

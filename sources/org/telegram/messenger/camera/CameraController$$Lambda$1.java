package org.telegram.messenger.camera;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class CameraController$$Lambda$1 implements Runnable {
    private final Runnable arg$1;
    private final CameraSession arg$2;
    private final CountDownLatch arg$3;

    CameraController$$Lambda$1(Runnable runnable, CameraSession cameraSession, CountDownLatch countDownLatch) {
        this.arg$1 = runnable;
        this.arg$2 = cameraSession;
        this.arg$3 = countDownLatch;
    }

    public void run() {
        CameraController.lambda$close$4$CameraController(this.arg$1, this.arg$2, this.arg$3);
    }
}

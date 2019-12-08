package org.telegram.messenger.camera;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$OPlkNPWb4gYZLA_NLxCoTx8pFu8 implements Runnable {
    private final /* synthetic */ Runnable f$0;
    private final /* synthetic */ CameraSession f$1;
    private final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ -$$Lambda$CameraController$OPlkNPWb4gYZLA_NLxCoTx8pFu8(Runnable runnable, CameraSession cameraSession, CountDownLatch countDownLatch) {
        this.f$0 = runnable;
        this.f$1 = cameraSession;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        CameraController.lambda$close$5(this.f$0, this.f$1, this.f$2);
    }
}

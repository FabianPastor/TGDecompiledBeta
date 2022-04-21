package org.webrtc;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class EglRenderer$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ EglRenderer f$0;
    public final /* synthetic */ CountDownLatch f$1;

    public /* synthetic */ EglRenderer$$ExternalSyntheticLambda3(EglRenderer eglRenderer, CountDownLatch countDownLatch) {
        this.f$0 = eglRenderer;
        this.f$1 = countDownLatch;
    }

    public final void run() {
        this.f$0.m4617lambda$release$1$orgwebrtcEglRenderer(this.f$1);
    }
}

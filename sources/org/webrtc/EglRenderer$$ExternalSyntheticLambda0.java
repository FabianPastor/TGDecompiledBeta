package org.webrtc;

public final /* synthetic */ class EglRenderer$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ EglRenderer f$0;

    public /* synthetic */ EglRenderer$$ExternalSyntheticLambda0(EglRenderer eglRenderer) {
        this.f$0 = eglRenderer;
    }

    public final void run() {
        this.f$0.renderFrameOnRenderThread();
    }
}

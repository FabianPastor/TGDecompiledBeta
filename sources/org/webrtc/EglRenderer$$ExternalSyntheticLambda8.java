package org.webrtc;

public final /* synthetic */ class EglRenderer$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ EglRenderer f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ EglRenderer$$ExternalSyntheticLambda8(EglRenderer eglRenderer, boolean z, Runnable runnable) {
        this.f$0 = eglRenderer;
        this.f$1 = z;
        this.f$2 = runnable;
    }

    public final void run() {
        this.f$0.lambda$releaseEglSurface$5(this.f$1, this.f$2);
    }
}

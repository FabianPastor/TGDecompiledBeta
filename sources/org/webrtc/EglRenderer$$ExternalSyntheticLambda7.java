package org.webrtc;

import org.webrtc.EglRenderer;
import org.webrtc.RendererCommon;

public final /* synthetic */ class EglRenderer$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ EglRenderer f$0;
    public final /* synthetic */ RendererCommon.GlDrawer f$1;
    public final /* synthetic */ EglRenderer.FrameListener f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ EglRenderer$$ExternalSyntheticLambda7(EglRenderer eglRenderer, RendererCommon.GlDrawer glDrawer, EglRenderer.FrameListener frameListener, float f, boolean z) {
        this.f$0 = eglRenderer;
        this.f$1 = glDrawer;
        this.f$2 = frameListener;
        this.f$3 = f;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.m4613lambda$addFrameListener$3$orgwebrtcEglRenderer(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

package org.webrtc;

public final /* synthetic */ class TextureViewRenderer$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ TextureViewRenderer f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ TextureViewRenderer$$ExternalSyntheticLambda1(TextureViewRenderer textureViewRenderer, int i, int i2, int i3, int i4) {
        this.f$0 = textureViewRenderer;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = i4;
    }

    public final void run() {
        this.f$0.lambda$onFrameResolutionChanged$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

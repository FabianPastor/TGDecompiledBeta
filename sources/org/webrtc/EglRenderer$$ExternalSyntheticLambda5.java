package org.webrtc;

import org.webrtc.EglBase;

public final /* synthetic */ class EglRenderer$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ EglRenderer f$0;
    public final /* synthetic */ EglBase.Context f$1;
    public final /* synthetic */ int[] f$2;

    public /* synthetic */ EglRenderer$$ExternalSyntheticLambda5(EglRenderer eglRenderer, EglBase.Context context, int[] iArr) {
        this.f$0 = eglRenderer;
        this.f$1 = context;
        this.f$2 = iArr;
    }

    public final void run() {
        this.f$0.lambda$init$0(this.f$1, this.f$2);
    }
}

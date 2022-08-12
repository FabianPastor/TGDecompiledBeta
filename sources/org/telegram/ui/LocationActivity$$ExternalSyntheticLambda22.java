package org.telegram.ui;

import android.opengl.GLSurfaceView;
import android.view.ViewGroup;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ ViewGroup f$1;
    public final /* synthetic */ GLSurfaceView f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda22(LocationActivity locationActivity, ViewGroup viewGroup, GLSurfaceView gLSurfaceView) {
        this.f$0 = locationActivity;
        this.f$1 = viewGroup;
        this.f$2 = gLSurfaceView;
    }

    public final void run() {
        this.f$0.lambda$onCheckGlScreenshot$41(this.f$1, this.f$2);
    }
}

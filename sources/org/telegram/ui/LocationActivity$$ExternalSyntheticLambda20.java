package org.telegram.ui;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ Bitmap f$1;
    public final /* synthetic */ GLSurfaceView f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda20(LocationActivity locationActivity, Bitmap bitmap, GLSurfaceView gLSurfaceView) {
        this.f$0 = locationActivity;
        this.f$1 = bitmap;
        this.f$2 = gLSurfaceView;
    }

    public final void run() {
        this.f$0.lambda$onCheckGlScreenshot$42(this.f$1, this.f$2);
    }
}

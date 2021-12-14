package org.telegram.ui.Components;

import android.graphics.SurfaceTexture;
import org.telegram.ui.Components.InstantCameraView;

public final /* synthetic */ class InstantCameraView$CameraGLThread$$ExternalSyntheticLambda0 implements SurfaceTexture.OnFrameAvailableListener {
    public final /* synthetic */ InstantCameraView.CameraGLThread f$0;

    public /* synthetic */ InstantCameraView$CameraGLThread$$ExternalSyntheticLambda0(InstantCameraView.CameraGLThread cameraGLThread) {
        this.f$0 = cameraGLThread;
    }

    public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.f$0.lambda$initGL$0(surfaceTexture);
    }
}

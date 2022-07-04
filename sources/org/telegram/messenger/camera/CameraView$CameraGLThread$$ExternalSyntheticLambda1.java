package org.telegram.messenger.camera;

import android.graphics.SurfaceTexture;
import org.telegram.messenger.camera.CameraView;

public final /* synthetic */ class CameraView$CameraGLThread$$ExternalSyntheticLambda1 implements SurfaceTexture.OnFrameAvailableListener {
    public final /* synthetic */ CameraView.CameraGLThread f$0;

    public /* synthetic */ CameraView$CameraGLThread$$ExternalSyntheticLambda1(CameraView.CameraGLThread cameraGLThread) {
        this.f$0 = cameraGLThread;
    }

    public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.f$0.lambda$handleMessage$2(surfaceTexture);
    }
}

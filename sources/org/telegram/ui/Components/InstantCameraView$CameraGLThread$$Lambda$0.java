package org.telegram.ui.Components;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import org.telegram.ui.Components.InstantCameraView.CameraGLThread;

final /* synthetic */ class InstantCameraView$CameraGLThread$$Lambda$0 implements OnFrameAvailableListener {
    private final CameraGLThread arg$1;

    InstantCameraView$CameraGLThread$$Lambda$0(CameraGLThread cameraGLThread) {
        this.arg$1 = cameraGLThread;
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.arg$1.lambda$initGL$0$InstantCameraView$CameraGLThread(surfaceTexture);
    }
}

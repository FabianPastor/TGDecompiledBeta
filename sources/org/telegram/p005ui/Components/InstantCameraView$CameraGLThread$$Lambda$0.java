package org.telegram.p005ui.Components;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import org.telegram.p005ui.Components.InstantCameraView.CameraGLThread;

/* renamed from: org.telegram.ui.Components.InstantCameraView$CameraGLThread$$Lambda$0 */
final /* synthetic */ class InstantCameraView$CameraGLThread$$Lambda$0 implements OnFrameAvailableListener {
    private final CameraGLThread arg$1;

    InstantCameraView$CameraGLThread$$Lambda$0(CameraGLThread cameraGLThread) {
        this.arg$1 = cameraGLThread;
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.arg$1.lambda$initGL$0$InstantCameraView$CameraGLThread(surfaceTexture);
    }
}

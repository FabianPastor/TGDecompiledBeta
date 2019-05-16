package org.telegram.ui.Components;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import org.telegram.ui.Components.InstantCameraView.CameraGLThread;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$InstantCameraView$CameraGLThread$8CYV7PTMH7zxhUymqIagAcuf5w8 implements OnFrameAvailableListener {
    private final /* synthetic */ CameraGLThread f$0;

    public /* synthetic */ -$$Lambda$InstantCameraView$CameraGLThread$8CYV7PTMH7zxhUymqIagAcuf5w8(CameraGLThread cameraGLThread) {
        this.f$0 = cameraGLThread;
    }

    public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.f$0.lambda$handleMessage$1$InstantCameraView$CameraGLThread(surfaceTexture);
    }
}

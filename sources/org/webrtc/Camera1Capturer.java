package org.webrtc;

import android.content.Context;
import org.webrtc.CameraSession;
import org.webrtc.CameraVideoCapturer;

public class Camera1Capturer extends CameraCapturer {
    private final boolean captureToTexture;

    public /* bridge */ /* synthetic */ void changeCaptureFormat(int i, int i2, int i3) {
        super.changeCaptureFormat(i, i2, i3);
    }

    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    public /* bridge */ /* synthetic */ void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver) {
        super.initialize(surfaceTextureHelper, context, capturerObserver);
    }

    public /* bridge */ /* synthetic */ boolean isScreencast() {
        return super.isScreencast();
    }

    public /* bridge */ /* synthetic */ void printStackTrace() {
        super.printStackTrace();
    }

    public /* bridge */ /* synthetic */ void startCapture(int i, int i2, int i3) {
        super.startCapture(i, i2, i3);
    }

    public /* bridge */ /* synthetic */ void stopCapture() {
        super.stopCapture();
    }

    public /* bridge */ /* synthetic */ void switchCamera(CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
        super.switchCamera(cameraSwitchHandler);
    }

    public /* bridge */ /* synthetic */ void switchCamera(CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler, String str) {
        super.switchCamera(cameraSwitchHandler, str);
    }

    public Camera1Capturer(String str, CameraVideoCapturer.CameraEventsHandler cameraEventsHandler, boolean z) {
        super(str, cameraEventsHandler, new Camera1Enumerator(z));
        this.captureToTexture = z;
    }

    /* access modifiers changed from: protected */
    public void createCameraSession(CameraSession.CreateSessionCallback createSessionCallback, CameraSession.Events events, Context context, SurfaceTextureHelper surfaceTextureHelper, String str, int i, int i2, int i3) {
        Camera1Session.create(createSessionCallback, events, this.captureToTexture, context, surfaceTextureHelper, Camera1Enumerator.getCameraIndex(str), i, i2, i3);
    }
}

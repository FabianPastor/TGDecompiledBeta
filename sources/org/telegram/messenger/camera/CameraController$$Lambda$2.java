package org.telegram.messenger.camera;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import java.io.File;

final /* synthetic */ class CameraController$$Lambda$2 implements PictureCallback {
    private final File arg$1;
    private final CameraInfo arg$2;
    private final boolean arg$3;
    private final Runnable arg$4;

    CameraController$$Lambda$2(File file, CameraInfo cameraInfo, boolean z, Runnable runnable) {
        this.arg$1 = file;
        this.arg$2 = cameraInfo;
        this.arg$3 = z;
        this.arg$4 = runnable;
    }

    public void onPictureTaken(byte[] bArr, Camera camera) {
        CameraController.lambda$takePicture$5$CameraController(this.arg$1, this.arg$2, this.arg$3, this.arg$4, bArr, camera);
    }
}

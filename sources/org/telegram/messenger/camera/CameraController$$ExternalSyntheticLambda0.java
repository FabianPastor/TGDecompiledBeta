package org.telegram.messenger.camera;

import android.hardware.Camera;
import java.io.File;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda0 implements Camera.PictureCallback {
    public final /* synthetic */ File f$0;
    public final /* synthetic */ CameraInfo f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda0(File file, CameraInfo cameraInfo, boolean z, Runnable runnable) {
        this.f$0 = file;
        this.f$1 = cameraInfo;
        this.f$2 = z;
        this.f$3 = runnable;
    }

    public final void onPictureTaken(byte[] bArr, Camera camera) {
        CameraController.lambda$takePicture$6(this.f$0, this.f$1, this.f$2, this.f$3, bArr, camera);
    }
}

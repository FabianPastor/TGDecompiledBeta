package org.telegram.messenger.camera;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$c9CThCYGhCTBXh4Mr7ku0l0BAnk implements PictureCallback {
    private final /* synthetic */ File f$0;
    private final /* synthetic */ CameraInfo f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$CameraController$c9CThCYGhCTBXh4Mr7ku0l0BAnk(File file, CameraInfo cameraInfo, boolean z, Runnable runnable) {
        this.f$0 = file;
        this.f$1 = cameraInfo;
        this.f$2 = z;
        this.f$3 = runnable;
    }

    public final void onPictureTaken(byte[] bArr, Camera camera) {
        CameraController.lambda$takePicture$6(this.f$0, this.f$1, this.f$2, this.f$3, bArr, camera);
    }
}

package org.telegram.messenger.camera;

import android.graphics.Bitmap;
import java.io.File;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ CameraController f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ Bitmap f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda7(CameraController cameraController, File file, Bitmap bitmap, long j) {
        this.f$0 = cameraController;
        this.f$1 = file;
        this.f$2 = bitmap;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$finishRecordingVideo$14(this.f$1, this.f$2, this.f$3);
    }
}

package org.telegram.messenger.camera;

import android.graphics.Bitmap;
import java.io.File;

final /* synthetic */ class CameraController$$Lambda$8 implements Runnable {
    private final CameraController arg$1;
    private final File arg$2;
    private final Bitmap arg$3;
    private final long arg$4;

    CameraController$$Lambda$8(CameraController cameraController, File file, Bitmap bitmap, long j) {
        this.arg$1 = cameraController;
        this.arg$2 = file;
        this.arg$3 = bitmap;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$finishRecordingVideo$11$CameraController(this.arg$2, this.arg$3, this.arg$4);
    }
}

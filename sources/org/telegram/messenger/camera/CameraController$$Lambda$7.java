package org.telegram.messenger.camera;

import android.hardware.Camera;
import java.io.File;
import org.telegram.messenger.camera.CameraController.VideoTakeCallback;

final /* synthetic */ class CameraController$$Lambda$7 implements Runnable {
    private final CameraController arg$1;
    private final Camera arg$2;
    private final CameraSession arg$3;
    private final File arg$4;
    private final CameraInfo arg$5;
    private final VideoTakeCallback arg$6;
    private final Runnable arg$7;

    CameraController$$Lambda$7(CameraController cameraController, Camera camera, CameraSession cameraSession, File file, CameraInfo cameraInfo, VideoTakeCallback videoTakeCallback, Runnable runnable) {
        this.arg$1 = cameraController;
        this.arg$2 = camera;
        this.arg$3 = cameraSession;
        this.arg$4 = file;
        this.arg$5 = cameraInfo;
        this.arg$6 = videoTakeCallback;
        this.arg$7 = runnable;
    }

    public void run() {
        this.arg$1.lambda$recordVideo$10$CameraController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}

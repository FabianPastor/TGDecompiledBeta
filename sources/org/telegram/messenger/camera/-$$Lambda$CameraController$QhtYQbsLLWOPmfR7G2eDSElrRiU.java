package org.telegram.messenger.camera;

import android.hardware.Camera;
import java.io.File;
import org.telegram.messenger.camera.CameraController.VideoTakeCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$QhtYQbsLLWOPmfR7G2eDSElrRiU implements Runnable {
    private final /* synthetic */ CameraController f$0;
    private final /* synthetic */ Camera f$1;
    private final /* synthetic */ CameraSession f$2;
    private final /* synthetic */ File f$3;
    private final /* synthetic */ CameraInfo f$4;
    private final /* synthetic */ VideoTakeCallback f$5;
    private final /* synthetic */ Runnable f$6;

    public /* synthetic */ -$$Lambda$CameraController$QhtYQbsLLWOPmfR7G2eDSElrRiU(CameraController cameraController, Camera camera, CameraSession cameraSession, File file, CameraInfo cameraInfo, VideoTakeCallback videoTakeCallback, Runnable runnable) {
        this.f$0 = cameraController;
        this.f$1 = camera;
        this.f$2 = cameraSession;
        this.f$3 = file;
        this.f$4 = cameraInfo;
        this.f$5 = videoTakeCallback;
        this.f$6 = runnable;
    }

    public final void run() {
        this.f$0.lambda$recordVideo$10$CameraController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

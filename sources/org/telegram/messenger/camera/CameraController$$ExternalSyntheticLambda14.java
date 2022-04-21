package org.telegram.messenger.camera;

import android.hardware.Camera;
import java.io.File;
import org.telegram.messenger.camera.CameraController;

public final /* synthetic */ class CameraController$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ CameraController f$0;
    public final /* synthetic */ Camera f$1;
    public final /* synthetic */ CameraSession f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ File f$4;
    public final /* synthetic */ CameraInfo f$5;
    public final /* synthetic */ CameraController.VideoTakeCallback f$6;
    public final /* synthetic */ Runnable f$7;

    public /* synthetic */ CameraController$$ExternalSyntheticLambda14(CameraController cameraController, Camera camera, CameraSession cameraSession, boolean z, File file, CameraInfo cameraInfo, CameraController.VideoTakeCallback videoTakeCallback, Runnable runnable) {
        this.f$0 = cameraController;
        this.f$1 = camera;
        this.f$2 = cameraSession;
        this.f$3 = z;
        this.f$4 = file;
        this.f$5 = cameraInfo;
        this.f$6 = videoTakeCallback;
        this.f$7 = runnable;
    }

    public final void run() {
        this.f$0.m1119x8fCLASSNAME(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}

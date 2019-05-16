package org.telegram.messenger.camera;

import android.graphics.Bitmap;
import java.io.File;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraController$5HicrJ12U2CLASSNAMEGyHfU8XHEFg--A implements Runnable {
    private final /* synthetic */ CameraController f$0;
    private final /* synthetic */ File f$1;
    private final /* synthetic */ Bitmap f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$CameraController$5HicrJ12U2CLASSNAMEGyHfU8XHEFg--A(CameraController cameraController, File file, Bitmap bitmap, long j) {
        this.f$0 = cameraController;
        this.f$1 = file;
        this.f$2 = bitmap;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$finishRecordingVideo$11$CameraController(this.f$1, this.f$2, this.f$3);
    }
}

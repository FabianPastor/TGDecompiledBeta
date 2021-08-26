package org.telegram.ui;

import android.hardware.Camera;

public final /* synthetic */ class CameraScanActivity$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ CameraScanActivity f$0;
    public final /* synthetic */ byte[] f$1;
    public final /* synthetic */ Camera f$2;

    public /* synthetic */ CameraScanActivity$$ExternalSyntheticLambda8(CameraScanActivity cameraScanActivity, byte[] bArr, Camera camera) {
        this.f$0 = cameraScanActivity;
        this.f$1 = bArr;
        this.f$2 = camera;
    }

    public final void run() {
        this.f$0.lambda$onPreviewFrame$8(this.f$1, this.f$2);
    }
}

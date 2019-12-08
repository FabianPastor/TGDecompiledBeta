package org.telegram.ui;

import android.hardware.Camera;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CameraScanActivity$FPC8ls1Kvfk3YwhYqcwlb81o_CY implements Runnable {
    private final /* synthetic */ CameraScanActivity f$0;
    private final /* synthetic */ byte[] f$1;
    private final /* synthetic */ Camera f$2;

    public /* synthetic */ -$$Lambda$CameraScanActivity$FPC8ls1Kvfk3YwhYqcwlb81o_CY(CameraScanActivity cameraScanActivity, byte[] bArr, Camera camera) {
        this.f$0 = cameraScanActivity;
        this.f$1 = bArr;
        this.f$2 = camera;
    }

    public final void run() {
        this.f$0.lambda$onPreviewFrame$9$CameraScanActivity(this.f$1, this.f$2);
    }
}

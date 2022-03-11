package org.telegram.ui;

import org.telegram.ui.CameraScanActivity;

public final /* synthetic */ class CameraScanActivity$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ CameraScanActivity f$0;
    public final /* synthetic */ CameraScanActivity.QrResult f$1;

    public /* synthetic */ CameraScanActivity$$ExternalSyntheticLambda17(CameraScanActivity cameraScanActivity, CameraScanActivity.QrResult qrResult) {
        this.f$0 = cameraScanActivity;
        this.f$1 = qrResult;
    }

    public final void run() {
        this.f$0.lambda$processShot$13(this.f$1);
    }
}

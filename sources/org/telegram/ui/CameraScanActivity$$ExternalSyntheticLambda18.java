package org.telegram.ui;

import org.telegram.ui.CameraScanActivity;

public final /* synthetic */ class CameraScanActivity$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ CameraScanActivity f$0;
    public final /* synthetic */ CameraScanActivity.QrResult f$1;

    public /* synthetic */ CameraScanActivity$$ExternalSyntheticLambda18(CameraScanActivity cameraScanActivity, CameraScanActivity.QrResult qrResult) {
        this.f$0 = cameraScanActivity;
        this.f$1 = qrResult;
    }

    public final void run() {
        this.f$0.lambda$processShot$14(this.f$1);
    }
}

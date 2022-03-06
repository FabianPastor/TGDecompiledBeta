package org.telegram.ui;

import org.telegram.messenger.MrzRecognizer;

public final /* synthetic */ class CameraScanActivity$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ CameraScanActivity f$0;
    public final /* synthetic */ MrzRecognizer.Result f$1;

    public /* synthetic */ CameraScanActivity$$ExternalSyntheticLambda11(CameraScanActivity cameraScanActivity, MrzRecognizer.Result result) {
        this.f$0 = cameraScanActivity;
        this.f$1 = result;
    }

    public final void run() {
        this.f$0.lambda$onPreviewFrame$8(this.f$1);
    }
}

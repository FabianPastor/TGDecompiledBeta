package org.telegram.ui;

import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.ui.MrzCameraActivity.MrzCameraActivityDelegate;

final /* synthetic */ class PassportActivity$$Lambda$61 implements MrzCameraActivityDelegate {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$61(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public void didFindMrzInfo(Result result) {
        this.arg$1.lambda$null$44$PassportActivity(result);
    }
}

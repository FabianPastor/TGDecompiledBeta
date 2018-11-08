package org.telegram.p005ui;

import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.p005ui.MrzCameraActivity.MrzCameraActivityDelegate;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$60 */
final /* synthetic */ class PassportActivity$$Lambda$60 implements MrzCameraActivityDelegate {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$60(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public void didFindMrzInfo(Result result) {
        this.arg$1.lambda$null$43$PassportActivity(result);
    }
}

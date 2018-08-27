package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$10 implements RequestDelegate {
    private final TwoStepVerificationActivity arg$1;

    TwoStepVerificationActivity$$Lambda$10(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.arg$1 = twoStepVerificationActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$processDone$31$TwoStepVerificationActivity(tLObject, tL_error);
    }
}

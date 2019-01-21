package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$9 implements RequestDelegate {
    private final TwoStepVerificationActivity arg$1;

    TwoStepVerificationActivity$$Lambda$9(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.arg$1 = twoStepVerificationActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$setNewPassword$18$TwoStepVerificationActivity(tLObject, tL_error);
    }
}

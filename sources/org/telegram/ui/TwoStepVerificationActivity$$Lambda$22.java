package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$22 implements RequestDelegate {
    private final TwoStepVerificationActivity arg$1;

    TwoStepVerificationActivity$$Lambda$22(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.arg$1 = twoStepVerificationActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$30$TwoStepVerificationActivity(tLObject, tL_error);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$21 implements RequestDelegate {
    private final TwoStepVerificationActivity arg$1;
    private final boolean arg$2;

    TwoStepVerificationActivity$$Lambda$21(TwoStepVerificationActivity twoStepVerificationActivity, boolean z) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$15$TwoStepVerificationActivity(this.arg$2, tLObject, tL_error);
    }
}

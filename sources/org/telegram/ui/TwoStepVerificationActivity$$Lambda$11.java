package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$11 implements RequestDelegate {
    static final RequestDelegate $instance = new TwoStepVerificationActivity$$Lambda$11();

    private TwoStepVerificationActivity$$Lambda$11() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        TwoStepVerificationActivity.lambda$checkSecretValues$26$TwoStepVerificationActivity(tLObject, tL_error);
    }
}

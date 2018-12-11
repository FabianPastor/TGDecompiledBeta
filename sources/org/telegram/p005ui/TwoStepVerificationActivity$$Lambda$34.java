package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.TwoStepVerificationActivity$$Lambda$34 */
final /* synthetic */ class TwoStepVerificationActivity$$Lambda$34 implements RequestDelegate {
    static final RequestDelegate $instance = new TwoStepVerificationActivity$$Lambda$34();

    private TwoStepVerificationActivity$$Lambda$34() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        TwoStepVerificationActivity.lambda$null$8$TwoStepVerificationActivity(tLObject, tL_error);
    }
}

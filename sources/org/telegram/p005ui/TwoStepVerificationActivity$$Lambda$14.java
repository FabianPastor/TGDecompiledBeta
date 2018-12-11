package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.TwoStepVerificationActivity$$Lambda$14 */
final /* synthetic */ class TwoStepVerificationActivity$$Lambda$14 implements RequestDelegate {
    private final TwoStepVerificationActivity arg$1;

    TwoStepVerificationActivity$$Lambda$14(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.arg$1 = twoStepVerificationActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$sendEmailConfirm$39$TwoStepVerificationActivity(tLObject, tL_error);
    }
}

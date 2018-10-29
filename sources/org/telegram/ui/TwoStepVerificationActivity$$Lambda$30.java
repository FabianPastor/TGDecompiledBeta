package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$30 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    TwoStepVerificationActivity$$Lambda$30(TwoStepVerificationActivity twoStepVerificationActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$2$TwoStepVerificationActivity(this.arg$2, this.arg$3);
    }
}

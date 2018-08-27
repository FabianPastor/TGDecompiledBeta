package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$24 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final boolean arg$4;

    TwoStepVerificationActivity$$Lambda$24(TwoStepVerificationActivity twoStepVerificationActivity, TL_error tL_error, TLObject tLObject, boolean z) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$null$14$TwoStepVerificationActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}

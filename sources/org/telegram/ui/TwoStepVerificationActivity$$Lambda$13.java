package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$13 implements RequestDelegate {
    private final TwoStepVerificationActivity arg$1;
    private final byte[] arg$2;
    private final byte[] arg$3;

    TwoStepVerificationActivity$$Lambda$13(TwoStepVerificationActivity twoStepVerificationActivity, byte[] bArr, byte[] bArr2) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = bArr;
        this.arg$3 = bArr2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$27$TwoStepVerificationActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}

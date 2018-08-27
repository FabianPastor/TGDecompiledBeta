package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$14 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final byte[] arg$2;
    private final TLObject arg$3;
    private final byte[] arg$4;

    TwoStepVerificationActivity$$Lambda$14(TwoStepVerificationActivity twoStepVerificationActivity, byte[] bArr, TLObject tLObject, byte[] bArr2) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = bArr;
        this.arg$3 = tLObject;
        this.arg$4 = bArr2;
    }

    public void run() {
        this.arg$1.lambda$null$23$TwoStepVerificationActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}

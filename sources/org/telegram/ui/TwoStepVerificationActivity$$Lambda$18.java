package org.telegram.ui;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$18 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final boolean arg$2;
    private final byte[] arg$3;

    TwoStepVerificationActivity$$Lambda$18(TwoStepVerificationActivity twoStepVerificationActivity, boolean z, byte[] bArr) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = z;
        this.arg$3 = bArr;
    }

    public void run() {
        this.arg$1.lambda$null$22$TwoStepVerificationActivity(this.arg$2, this.arg$3);
    }
}

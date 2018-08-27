package org.telegram.ui;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$9 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final byte[] arg$2;

    TwoStepVerificationActivity$$Lambda$9(TwoStepVerificationActivity twoStepVerificationActivity, byte[] bArr) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = bArr;
    }

    public void run() {
        this.arg$1.lambda$processDone$28$TwoStepVerificationActivity(this.arg$2);
    }
}

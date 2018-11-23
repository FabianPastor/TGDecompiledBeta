package org.telegram.p005ui;

/* renamed from: org.telegram.ui.TwoStepVerificationActivity$$Lambda$24 */
final /* synthetic */ class TwoStepVerificationActivity$$Lambda$24 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final boolean arg$2;
    private final byte[] arg$3;

    TwoStepVerificationActivity$$Lambda$24(TwoStepVerificationActivity twoStepVerificationActivity, boolean z, byte[] bArr) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = z;
        this.arg$3 = bArr;
    }

    public void run() {
        this.arg$1.lambda$null$27$TwoStepVerificationActivity(this.arg$2, this.arg$3);
    }
}

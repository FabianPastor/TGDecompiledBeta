package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$7 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final TL_account_updatePasswordSettings arg$2;
    private final boolean arg$3;
    private final String arg$4;

    TwoStepVerificationActivity$$Lambda$7(TwoStepVerificationActivity twoStepVerificationActivity, TL_account_updatePasswordSettings tL_account_updatePasswordSettings, boolean z, String str) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = tL_account_updatePasswordSettings;
        this.arg$3 = z;
        this.arg$4 = str;
    }

    public void run() {
        this.arg$1.lambda$setNewPassword$20$TwoStepVerificationActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}

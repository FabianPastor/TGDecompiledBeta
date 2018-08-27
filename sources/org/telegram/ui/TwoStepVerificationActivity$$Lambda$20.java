package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$20 implements Runnable {
    private final TwoStepVerificationActivity arg$1;
    private final TL_error arg$2;
    private final boolean arg$3;
    private final TLObject arg$4;
    private final byte[] arg$5;
    private final TL_account_updatePasswordSettings arg$6;

    TwoStepVerificationActivity$$Lambda$20(TwoStepVerificationActivity twoStepVerificationActivity, TL_error tL_error, boolean z, TLObject tLObject, byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = tL_error;
        this.arg$3 = z;
        this.arg$4 = tLObject;
        this.arg$5 = bArr;
        this.arg$6 = tL_account_updatePasswordSettings;
    }

    public void run() {
        this.arg$1.lambda$null$18$TwoStepVerificationActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}

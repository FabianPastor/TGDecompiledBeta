package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$19 implements RequestDelegate {
    private final TwoStepVerificationActivity arg$1;
    private final boolean arg$2;
    private final byte[] arg$3;
    private final TL_account_updatePasswordSettings arg$4;

    TwoStepVerificationActivity$$Lambda$19(TwoStepVerificationActivity twoStepVerificationActivity, boolean z, byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.arg$1 = twoStepVerificationActivity;
        this.arg$2 = z;
        this.arg$3 = bArr;
        this.arg$4 = tL_account_updatePasswordSettings;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$19$TwoStepVerificationActivity(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}

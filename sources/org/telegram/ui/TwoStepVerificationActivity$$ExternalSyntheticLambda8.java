package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ TLRPC.TL_account_updatePasswordSettings f$1;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda8(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tL_account_updatePasswordSettings;
    }

    public final void run() {
        this.f$0.m4726xvar_fa26(this.f$1);
    }
}

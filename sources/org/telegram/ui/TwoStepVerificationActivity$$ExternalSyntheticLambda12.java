package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ TLRPC$TL_account_updatePasswordSettings f$1;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda12(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tLRPC$TL_account_updatePasswordSettings;
    }

    public final void run() {
        this.f$0.lambda$clearPassword$24(this.f$1);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$processNext$34(this.f$1);
    }
}

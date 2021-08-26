package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda10(TwoStepVerificationActivity twoStepVerificationActivity, TLObject tLObject) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$resetPassword$9(this.f$1);
    }
}

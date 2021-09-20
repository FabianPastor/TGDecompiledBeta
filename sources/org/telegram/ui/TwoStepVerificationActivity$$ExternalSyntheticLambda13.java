package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda13(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$processDone$30(this.f$1);
    }
}

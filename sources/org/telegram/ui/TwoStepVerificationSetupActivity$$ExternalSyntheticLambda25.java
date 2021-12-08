package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda25(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$setNewPassword$30(this.f$1, this.f$2, this.f$3);
    }
}

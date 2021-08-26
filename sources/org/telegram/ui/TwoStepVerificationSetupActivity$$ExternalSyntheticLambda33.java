package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, boolean z) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$setNewPassword$31(this.f$1, tLObject, tLRPC$TL_error);
    }
}

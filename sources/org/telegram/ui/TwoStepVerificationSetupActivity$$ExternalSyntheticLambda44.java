package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda44 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda44(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity) {
        this.f$0 = twoStepVerificationSetupActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$5(tLObject, tLRPC$TL_error);
    }
}
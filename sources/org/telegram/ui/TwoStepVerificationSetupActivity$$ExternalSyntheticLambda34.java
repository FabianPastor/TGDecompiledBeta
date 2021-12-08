package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda34 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda34(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, String str) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$14(this.f$1, tLObject, tLRPC$TL_error);
    }
}

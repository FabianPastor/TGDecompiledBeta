package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda24 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationActivity f$0;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda24(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = twoStepVerificationActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$cancelPasswordReset$6(tLObject, tLRPC$TL_error);
    }
}
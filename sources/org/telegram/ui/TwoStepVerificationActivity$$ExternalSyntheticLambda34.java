package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda34 implements RequestDelegate {
    public static final /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda34 INSTANCE = new TwoStepVerificationActivity$$ExternalSyntheticLambda34();

    private /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda34() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TwoStepVerificationActivity.lambda$checkSecretValues$25(tLObject, tLRPC$TL_error);
    }
}

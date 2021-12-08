package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda27 implements RequestDelegate {
    public static final /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda27 INSTANCE = new TwoStepVerificationActivity$$ExternalSyntheticLambda27();

    private /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda27() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        TwoStepVerificationActivity.lambda$checkSecretValues$25(tLObject, tL_error);
    }
}

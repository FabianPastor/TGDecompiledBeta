package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda41 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda41(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, String str) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3423x2a0ff6b6(this.f$1, tLObject, tL_error);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda27 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda27(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, String str) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4011x5CLASSNAMEa(this.f$1, tLObject, tL_error);
    }
}

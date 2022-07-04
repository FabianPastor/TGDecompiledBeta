package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda42 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda42(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, boolean z) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4796xcvar_d503(this.f$1, tLObject, tL_error);
    }
}

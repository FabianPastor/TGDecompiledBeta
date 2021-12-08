package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda28 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda28(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, boolean z) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4035xd34578a7(this.f$1, tLObject, tL_error);
    }
}

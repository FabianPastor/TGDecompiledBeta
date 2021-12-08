package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda25 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda25(TwoStepVerificationActivity twoStepVerificationActivity, boolean z, boolean z2) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3989xCLASSNAMEbd8a5(this.f$1, this.f$2, tLObject, tL_error);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda45 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ byte[] f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda45(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, byte[] bArr) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = bArr;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3419x907ebdaf(this.f$1, tLObject, tL_error);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda37 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ byte[] f$1;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda37(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, byte[] bArr) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = bArr;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$11(this.f$1, tLObject, tLRPC$TL_error);
    }
}

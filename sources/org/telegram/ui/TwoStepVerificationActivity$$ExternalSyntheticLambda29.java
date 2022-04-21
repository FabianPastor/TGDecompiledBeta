package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda29 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ byte[] f$1;
    public final /* synthetic */ byte[] f$2;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda29(TwoStepVerificationActivity twoStepVerificationActivity, byte[] bArr, byte[] bArr2) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = bArr;
        this.f$2 = bArr2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3382xefe23dd9(this.f$1, this.f$2, tLObject, tL_error);
    }
}

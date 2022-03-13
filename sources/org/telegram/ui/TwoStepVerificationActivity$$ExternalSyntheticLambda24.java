package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda24(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z, boolean z2) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = z;
        this.f$4 = z2;
    }

    public final void run() {
        this.f$0.lambda$loadPasswordInfo$18(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

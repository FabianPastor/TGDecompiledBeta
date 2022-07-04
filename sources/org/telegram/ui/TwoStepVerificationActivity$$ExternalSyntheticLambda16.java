package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda16(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.TL_error tL_error, TLObject tLObject, boolean z, boolean z2) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = z;
        this.f$4 = z2;
    }

    public final void run() {
        this.f$0.m4734xfd9d5da7(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

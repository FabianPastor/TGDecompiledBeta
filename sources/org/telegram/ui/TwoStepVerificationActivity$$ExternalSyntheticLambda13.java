package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationActivity$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ TwoStepVerificationActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ TwoStepVerificationActivity$$ExternalSyntheticLambda13(TwoStepVerificationActivity twoStepVerificationActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = twoStepVerificationActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m4724xCLASSNAME(this.f$1, this.f$2);
    }
}

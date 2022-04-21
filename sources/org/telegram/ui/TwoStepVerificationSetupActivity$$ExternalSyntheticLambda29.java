package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m3407x89a077e(this.f$1, this.f$2);
    }
}

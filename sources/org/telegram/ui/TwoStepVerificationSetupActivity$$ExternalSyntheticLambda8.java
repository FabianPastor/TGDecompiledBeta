package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda8(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLObject tLObject, String str, TLRPC.TL_error tL_error) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tLObject;
        this.f$2 = str;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.m4010x1416db0b(this.f$1, this.f$2, this.f$3);
    }
}

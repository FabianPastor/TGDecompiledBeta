package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda30 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda30(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLObject tLObject, String str, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tLObject;
        this.f$2 = str;
        this.f$3 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$processNext$30(this.f$1, this.f$2, this.f$3);
    }
}

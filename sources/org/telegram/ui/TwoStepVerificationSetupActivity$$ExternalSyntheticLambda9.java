package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.TL_account_passwordInputSettings f$4;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda9(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLObject tLObject, boolean z, String str, TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tLObject;
        this.f$2 = z;
        this.f$3 = str;
        this.f$4 = tL_account_passwordInputSettings;
    }

    public final void run() {
        this.f$0.m4039x99eCLASSNAME(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

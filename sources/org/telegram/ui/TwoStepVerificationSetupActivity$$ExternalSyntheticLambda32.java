package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ byte[] f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ TLRPC.TL_account_passwordInputSettings f$6;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda32(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLRPC.TL_error tL_error, boolean z, TLObject tLObject, byte[] bArr, String str, TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tL_error;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = bArr;
        this.f$5 = str;
        this.f$6 = tL_account_passwordInputSettings;
    }

    public final void run() {
        this.f$0.m3433xb2742141(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

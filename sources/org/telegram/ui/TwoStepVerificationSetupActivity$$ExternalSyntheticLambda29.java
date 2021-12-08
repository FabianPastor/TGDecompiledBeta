package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ byte[] f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.TL_account_passwordInputSettings f$4;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, boolean z, byte[] bArr, String str, TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = z;
        this.f$2 = bArr;
        this.f$3 = str;
        this.f$4 = tL_account_passwordInputSettings;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4038xa8426b04(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}

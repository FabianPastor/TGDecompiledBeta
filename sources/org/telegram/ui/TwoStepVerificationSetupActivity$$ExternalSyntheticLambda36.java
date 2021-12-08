package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda36 implements RequestDelegate {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ byte[] f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC$TL_account_passwordInputSettings f$4;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda36(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, boolean z, byte[] bArr, String str, TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = z;
        this.f$2 = bArr;
        this.f$3 = str;
        this.f$4 = tLRPC$TL_account_passwordInputSettings;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$setNewPassword$34(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}

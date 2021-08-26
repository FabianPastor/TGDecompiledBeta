package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class TwoStepVerificationSetupActivity$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ TwoStepVerificationSetupActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ byte[] f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ TLRPC$TL_account_passwordInputSettings f$6;

    public /* synthetic */ TwoStepVerificationSetupActivity$$ExternalSyntheticLambda24(TwoStepVerificationSetupActivity twoStepVerificationSetupActivity, TLRPC$TL_error tLRPC$TL_error, boolean z, TLObject tLObject, byte[] bArr, String str, TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings) {
        this.f$0 = twoStepVerificationSetupActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = bArr;
        this.f$5 = str;
        this.f$6 = tLRPC$TL_account_passwordInputSettings;
    }

    public final void run() {
        this.f$0.lambda$setNewPassword$33(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

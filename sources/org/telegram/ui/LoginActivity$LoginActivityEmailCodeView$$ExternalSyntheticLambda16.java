package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_auth_resendCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityEmailCodeView f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;
    public final /* synthetic */ TLRPC$TL_auth_resendCode f$4;

    public /* synthetic */ LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda16(LoginActivity.LoginActivityEmailCodeView loginActivityEmailCodeView, TLObject tLObject, Bundle bundle, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode) {
        this.f$0 = loginActivityEmailCodeView;
        this.f$1 = tLObject;
        this.f$2 = bundle;
        this.f$3 = tLRPC$TL_error;
        this.f$4 = tLRPC$TL_auth_resendCode;
    }

    public final void run() {
        this.f$0.lambda$new$4(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

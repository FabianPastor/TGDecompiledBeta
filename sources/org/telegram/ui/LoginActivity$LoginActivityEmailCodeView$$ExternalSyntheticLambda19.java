package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_auth_resendCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda19 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivityEmailCodeView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ TLRPC$TL_auth_resendCode f$2;

    public /* synthetic */ LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda19(LoginActivity.LoginActivityEmailCodeView loginActivityEmailCodeView, Bundle bundle, TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode) {
        this.f$0 = loginActivityEmailCodeView;
        this.f$1 = bundle;
        this.f$2 = tLRPC$TL_auth_resendCode;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$5(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}

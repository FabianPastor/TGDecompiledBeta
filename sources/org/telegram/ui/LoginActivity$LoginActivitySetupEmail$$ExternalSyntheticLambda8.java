package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_sendVerifyEmailCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivitySetupEmail f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ TLRPC$TL_account_sendVerifyEmailCode f$2;

    public /* synthetic */ LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda8(LoginActivity.LoginActivitySetupEmail loginActivitySetupEmail, Bundle bundle, TLRPC$TL_account_sendVerifyEmailCode tLRPC$TL_account_sendVerifyEmailCode) {
        this.f$0 = loginActivitySetupEmail;
        this.f$1 = bundle;
        this.f$2 = tLRPC$TL_account_sendVerifyEmailCode;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$8(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}

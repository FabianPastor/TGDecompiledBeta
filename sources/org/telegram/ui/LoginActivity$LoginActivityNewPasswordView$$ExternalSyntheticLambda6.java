package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_auth_recoverPassword;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityNewPasswordView f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC$TL_auth_recoverPassword f$3;

    public /* synthetic */ LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6(LoginActivity.LoginActivityNewPasswordView loginActivityNewPasswordView, String str, String str2, TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword) {
        this.f$0 = loginActivityNewPasswordView;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = tLRPC$TL_auth_recoverPassword;
    }

    public final void run() {
        this.f$0.lambda$recoverPassword$9(this.f$1, this.f$2, this.f$3);
    }
}

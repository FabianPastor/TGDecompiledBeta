package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityNewPasswordView f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.TL_auth_recoverPassword f$3;

    public /* synthetic */ LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7(LoginActivity.LoginActivityNewPasswordView loginActivityNewPasswordView, String str, String str2, TLRPC.TL_auth_recoverPassword tL_auth_recoverPassword) {
        this.f$0 = loginActivityNewPasswordView;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = tL_auth_recoverPassword;
    }

    public final void run() {
        this.f$0.m3794x672b7637(this.f$1, this.f$2, this.f$3);
    }
}

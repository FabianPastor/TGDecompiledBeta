package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityNewPasswordView f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6(LoginActivity.LoginActivityNewPasswordView loginActivityNewPasswordView, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, String str2) {
        this.f$0 = loginActivityNewPasswordView;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = str;
        this.f$4 = str2;
    }

    public final void run() {
        this.f$0.lambda$recoverPassword$2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

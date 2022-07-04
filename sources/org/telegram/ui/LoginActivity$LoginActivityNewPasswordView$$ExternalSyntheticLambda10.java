package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivityNewPasswordView f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda10(LoginActivity.LoginActivityNewPasswordView loginActivityNewPasswordView, String str, String str2) {
        this.f$0 = loginActivityNewPasswordView;
        this.f$1 = str;
        this.f$2 = str2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3790x4f1CLASSNAMEbb(this.f$1, this.f$2, tLObject, tL_error);
    }
}

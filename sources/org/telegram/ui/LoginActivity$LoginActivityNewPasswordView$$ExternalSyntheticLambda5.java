package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityNewPasswordView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLObject f$4;

    public /* synthetic */ LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5(LoginActivity.LoginActivityNewPasswordView loginActivityNewPasswordView, TLRPC.TL_error tL_error, String str, String str2, TLObject tLObject) {
        this.f$0 = loginActivityNewPasswordView;
        this.f$1 = tL_error;
        this.f$2 = str;
        this.f$3 = str2;
        this.f$4 = tLObject;
    }

    public final void run() {
        this.f$0.m3194x4f1CLASSNAMEbb(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

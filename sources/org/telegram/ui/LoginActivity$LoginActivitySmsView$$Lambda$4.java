package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$4 implements RequestDelegate {
    private final LoginActivitySmsView arg$1;
    private final TL_auth_signIn arg$2;
    private final String arg$3;

    LoginActivity$LoginActivitySmsView$$Lambda$4(LoginActivitySmsView loginActivitySmsView, TL_auth_signIn tL_auth_signIn, String str) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_auth_signIn;
        this.arg$3 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$9$LoginActivity$LoginActivitySmsView(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}

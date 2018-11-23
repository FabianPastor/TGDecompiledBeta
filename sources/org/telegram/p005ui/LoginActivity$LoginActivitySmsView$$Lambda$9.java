package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivitySmsView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$$Lambda$9 */
final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$9 implements RequestDelegate {
    private final LoginActivitySmsView arg$1;
    private final TL_auth_signIn arg$2;

    LoginActivity$LoginActivitySmsView$$Lambda$9(LoginActivitySmsView loginActivitySmsView, TL_auth_signIn tL_auth_signIn) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_auth_signIn;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$6$LoginActivity$LoginActivitySmsView(this.arg$2, tLObject, tL_error);
    }
}

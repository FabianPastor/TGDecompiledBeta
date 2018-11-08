package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivityPasswordView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$Lambda$5 */
final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$Lambda$5 implements RequestDelegate {
    private final LoginActivityPasswordView arg$1;

    LoginActivity$LoginActivityPasswordView$$Lambda$5(LoginActivityPasswordView loginActivityPasswordView) {
        this.arg$1 = loginActivityPasswordView;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$12$LoginActivity$LoginActivityPasswordView(tLObject, tL_error);
    }
}

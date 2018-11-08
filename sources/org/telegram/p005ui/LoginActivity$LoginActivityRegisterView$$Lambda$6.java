package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivityRegisterView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$Lambda$6 */
final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$Lambda$6 implements RequestDelegate {
    private final LoginActivityRegisterView arg$1;

    LoginActivity$LoginActivityRegisterView$$Lambda$6(LoginActivityRegisterView loginActivityRegisterView) {
        this.arg$1 = loginActivityRegisterView;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$10$LoginActivity$LoginActivityRegisterView(tLObject, tL_error);
    }
}

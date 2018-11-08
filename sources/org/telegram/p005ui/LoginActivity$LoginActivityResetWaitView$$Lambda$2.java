package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivityResetWaitView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$Lambda$2 */
final /* synthetic */ class LoginActivity$LoginActivityResetWaitView$$Lambda$2 implements RequestDelegate {
    private final LoginActivityResetWaitView arg$1;

    LoginActivity$LoginActivityResetWaitView$$Lambda$2(LoginActivityResetWaitView loginActivityResetWaitView) {
        this.arg$1 = loginActivityResetWaitView;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$LoginActivity$LoginActivityResetWaitView(tLObject, tL_error);
    }
}

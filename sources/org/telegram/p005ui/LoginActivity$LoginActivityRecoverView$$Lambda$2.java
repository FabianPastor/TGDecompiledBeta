package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivityRecoverView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$Lambda$2 */
final /* synthetic */ class LoginActivity$LoginActivityRecoverView$$Lambda$2 implements RequestDelegate {
    private final LoginActivityRecoverView arg$1;

    LoginActivity$LoginActivityRecoverView$$Lambda$2(LoginActivityRecoverView loginActivityRecoverView) {
        this.arg$1 = loginActivityRecoverView;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$4$LoginActivity$LoginActivityRecoverView(tLObject, tL_error);
    }
}

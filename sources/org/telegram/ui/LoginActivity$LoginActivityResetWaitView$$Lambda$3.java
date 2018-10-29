package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityResetWaitView;

final /* synthetic */ class LoginActivity$LoginActivityResetWaitView$$Lambda$3 implements Runnable {
    private final LoginActivityResetWaitView arg$1;
    private final TL_error arg$2;

    LoginActivity$LoginActivityResetWaitView$$Lambda$3(LoginActivityResetWaitView loginActivityResetWaitView, TL_error tL_error) {
        this.arg$1 = loginActivityResetWaitView;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$LoginActivity$LoginActivityResetWaitView(this.arg$2);
    }
}

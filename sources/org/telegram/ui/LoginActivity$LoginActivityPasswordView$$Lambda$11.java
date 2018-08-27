package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityPasswordView;

final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$Lambda$11 implements Runnable {
    private final LoginActivityPasswordView arg$1;
    private final TL_error arg$2;

    LoginActivity$LoginActivityPasswordView$$Lambda$11(LoginActivityPasswordView loginActivityPasswordView, TL_error tL_error) {
        this.arg$1 = loginActivityPasswordView;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$5$LoginActivity$LoginActivityPasswordView(this.arg$2);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityPasswordView;

final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$Lambda$6 implements Runnable {
    private final LoginActivityPasswordView arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    LoginActivity$LoginActivityPasswordView$$Lambda$6(LoginActivityPasswordView loginActivityPasswordView, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = loginActivityPasswordView;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$11$LoginActivity$LoginActivityPasswordView(this.arg$2, this.arg$3);
    }
}

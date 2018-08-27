package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRegisterView;

final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$Lambda$7 implements Runnable {
    private final LoginActivityRegisterView arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    LoginActivity$LoginActivityRegisterView$$Lambda$7(LoginActivityRegisterView loginActivityRegisterView, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = loginActivityRegisterView;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$9$LoginActivity$LoginActivityRegisterView(this.arg$2, this.arg$3);
    }
}

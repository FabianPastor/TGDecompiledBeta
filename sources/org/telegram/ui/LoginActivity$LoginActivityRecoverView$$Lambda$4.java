package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivityRecoverView;

final /* synthetic */ class LoginActivity$LoginActivityRecoverView$$Lambda$4 implements Runnable {
    private final LoginActivityRecoverView arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    LoginActivity$LoginActivityRecoverView$$Lambda$4(LoginActivityRecoverView loginActivityRecoverView, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = loginActivityRecoverView;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$3$LoginActivity$LoginActivityRecoverView(this.arg$2, this.arg$3);
    }
}

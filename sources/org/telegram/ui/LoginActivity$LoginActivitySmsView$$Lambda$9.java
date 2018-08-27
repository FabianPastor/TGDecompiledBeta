package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$9 implements Runnable {
    private final LoginActivitySmsView arg$1;
    private final TL_error arg$2;
    private final Bundle arg$3;
    private final TLObject arg$4;

    LoginActivity$LoginActivitySmsView$$Lambda$9(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, Bundle bundle, TLObject tLObject) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_error;
        this.arg$3 = bundle;
        this.arg$4 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$4$LoginActivity$LoginActivitySmsView(this.arg$2, this.arg$3, this.arg$4);
    }
}

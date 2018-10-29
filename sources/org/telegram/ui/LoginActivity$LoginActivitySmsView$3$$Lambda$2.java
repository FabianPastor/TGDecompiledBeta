package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView.C14623;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$3$$Lambda$2 implements Runnable {
    private final C14623 arg$1;
    private final TL_error arg$2;

    LoginActivity$LoginActivitySmsView$3$$Lambda$2(C14623 c14623, TL_error tL_error) {
        this.arg$1 = c14623;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$LoginActivity$LoginActivitySmsView$3(this.arg$2);
    }
}

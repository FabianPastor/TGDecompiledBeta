package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView.C10823;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$3$$Lambda$2 implements Runnable {
    private final C10823 arg$1;
    private final TL_error arg$2;

    LoginActivity$LoginActivitySmsView$3$$Lambda$2(C10823 c10823, TL_error tL_error) {
        this.arg$1 = c10823;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$LoginActivity$LoginActivitySmsView$3(this.arg$2);
    }
}

package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivitySmsView.CLASSNAME;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$3$$Lambda$2 */
final /* synthetic */ class LoginActivity$LoginActivitySmsView$3$$Lambda$2 implements Runnable {
    private final CLASSNAME arg$1;
    private final TL_error arg$2;

    LoginActivity$LoginActivitySmsView$3$$Lambda$2(CLASSNAME CLASSNAME, TL_error tL_error) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$LoginActivity$LoginActivitySmsView$3(this.arg$2);
    }
}

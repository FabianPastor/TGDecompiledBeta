package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivitySmsView.C18313;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$3$$Lambda$2 */
final /* synthetic */ class LoginActivity$LoginActivitySmsView$3$$Lambda$2 implements Runnable {
    private final C18313 arg$1;
    private final TL_error arg$2;

    LoginActivity$LoginActivitySmsView$3$$Lambda$2(C18313 c18313, TL_error tL_error) {
        this.arg$1 = c18313;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$LoginActivity$LoginActivitySmsView$3(this.arg$2);
    }
}

package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivitySmsView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_auth_signIn;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$$Lambda$8 */
final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$8 implements Runnable {
    private final LoginActivitySmsView arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final TL_auth_signIn arg$4;
    private final String arg$5;

    LoginActivity$LoginActivitySmsView$$Lambda$8(LoginActivitySmsView loginActivitySmsView, TL_error tL_error, TLObject tLObject, TL_auth_signIn tL_auth_signIn, String str) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = tL_auth_signIn;
        this.arg$5 = str;
    }

    public void run() {
        this.arg$1.lambda$null$7$LoginActivity$LoginActivitySmsView(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

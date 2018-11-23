package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivitySmsView.C09843;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$3$$Lambda$1 */
final /* synthetic */ class LoginActivity$LoginActivitySmsView$3$$Lambda$1 implements RequestDelegate {
    private final C09843 arg$1;

    LoginActivity$LoginActivitySmsView$3$$Lambda$1(C09843 c09843) {
        this.arg$1 = c09843;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$LoginActivity$LoginActivitySmsView$3(tLObject, tL_error);
    }
}

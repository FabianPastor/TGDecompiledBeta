package org.telegram.p005ui;

import org.telegram.p005ui.LoginActivity.LoginActivitySmsView;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$$Lambda$6 */
final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$6 implements RequestDelegate {
    static final RequestDelegate $instance = new LoginActivity$LoginActivitySmsView$$Lambda$6();

    private LoginActivity$LoginActivitySmsView$$Lambda$6() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        LoginActivitySmsView.lambda$onBackPressed$10$LoginActivity$LoginActivitySmsView(tLObject, tL_error);
    }
}

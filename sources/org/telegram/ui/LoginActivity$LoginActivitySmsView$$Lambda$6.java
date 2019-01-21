package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$6 implements RequestDelegate {
    static final RequestDelegate $instance = new LoginActivity$LoginActivitySmsView$$Lambda$6();

    private LoginActivity$LoginActivitySmsView$$Lambda$6() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        LoginActivitySmsView.lambda$onBackPressed$10$LoginActivity$LoginActivitySmsView(tLObject, tL_error);
    }
}

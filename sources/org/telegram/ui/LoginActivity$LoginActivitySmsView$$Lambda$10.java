package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$10 implements RequestDelegate {
    static final RequestDelegate $instance = new LoginActivity$LoginActivitySmsView$$Lambda$10();

    private LoginActivity$LoginActivitySmsView$$Lambda$10() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        LoginActivitySmsView.lambda$null$2$LoginActivity$LoginActivitySmsView(tLObject, tL_error);
    }
}

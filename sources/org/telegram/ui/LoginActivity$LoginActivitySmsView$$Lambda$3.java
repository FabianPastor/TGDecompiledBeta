package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$$Lambda$3 implements RequestDelegate {
    private final LoginActivitySmsView arg$1;
    private final Bundle arg$2;

    LoginActivity$LoginActivitySmsView$$Lambda$3(LoginActivitySmsView loginActivitySmsView, Bundle bundle) {
        this.arg$1 = loginActivitySmsView;
        this.arg$2 = bundle;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$resendCode$5$LoginActivity$LoginActivitySmsView(this.arg$2, tLObject, tL_error);
    }
}

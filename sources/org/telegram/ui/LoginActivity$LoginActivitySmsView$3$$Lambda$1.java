package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.LoginActivity.LoginActivitySmsView.C10823;

final /* synthetic */ class LoginActivity$LoginActivitySmsView$3$$Lambda$1 implements RequestDelegate {
    private final C10823 arg$1;

    LoginActivity$LoginActivitySmsView$3$$Lambda$1(C10823 c10823) {
        this.arg$1 = c10823;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$LoginActivity$LoginActivitySmsView$3(tLObject, tL_error);
    }
}

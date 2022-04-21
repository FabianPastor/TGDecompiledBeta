package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda17(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m2526lambda$new$1$orgtelegramuiLoginActivity$LoginActivitySmsView(this.f$1);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m3859x619ab9c(this.f$1, this.f$2);
    }
}

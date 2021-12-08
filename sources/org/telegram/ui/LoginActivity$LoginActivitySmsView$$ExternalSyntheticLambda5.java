package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_auth_signIn f$3;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_auth_signIn tL_auth_signIn) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_auth_signIn;
    }

    public final void run() {
        this.f$0.m3243xae6e8var_(this.f$1, this.f$2, this.f$3);
    }
}

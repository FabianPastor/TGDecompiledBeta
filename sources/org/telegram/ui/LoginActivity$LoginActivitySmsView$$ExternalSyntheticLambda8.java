package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_auth_signIn f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_auth_signIn tL_auth_signIn) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_auth_signIn;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3244xdCLASSNAMEc7(this.f$1, tLObject, tL_error);
    }
}

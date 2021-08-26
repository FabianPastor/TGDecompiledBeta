package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_auth_signIn;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC$TL_auth_signIn f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tLRPC$TL_auth_signIn;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$6(this.f$1, tLObject, tLRPC$TL_error);
    }
}

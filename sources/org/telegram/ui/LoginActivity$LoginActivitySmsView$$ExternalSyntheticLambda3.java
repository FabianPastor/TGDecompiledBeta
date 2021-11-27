package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_auth_signIn;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ TLRPC$TL_auth_signIn f$3;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_auth_signIn tLRPC$TL_auth_signIn) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = tLRPC$TL_auth_signIn;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$5(this.f$1, this.f$2, this.f$3);
    }
}

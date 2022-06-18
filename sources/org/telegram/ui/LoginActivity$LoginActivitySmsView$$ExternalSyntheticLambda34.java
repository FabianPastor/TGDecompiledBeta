package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda34 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda34(LoginActivity.LoginActivitySmsView loginActivitySmsView) {
        this.f$0 = loginActivitySmsView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$22(tLObject, tLRPC$TL_error);
    }
}

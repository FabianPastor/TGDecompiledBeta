package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda38 implements RequestDelegate {
    public static final /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda38 INSTANCE = new LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda38();

    private /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda38() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LoginActivity.LoginActivitySmsView.lambda$onBackPressed$37(tLObject, tLRPC$TL_error);
    }
}

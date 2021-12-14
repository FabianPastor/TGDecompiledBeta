package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivityPasswordView f$0;

    public /* synthetic */ LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12(LoginActivity.LoginActivityPasswordView loginActivityPasswordView) {
        this.f$0 = loginActivityPasswordView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$13(tLObject, tLRPC$TL_error);
    }
}

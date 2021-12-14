package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivityRegisterView f$0;

    public /* synthetic */ LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15(LoginActivity.LoginActivityRegisterView loginActivityRegisterView) {
        this.f$0 = loginActivityRegisterView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$15(tLObject, tLRPC$TL_error);
    }
}

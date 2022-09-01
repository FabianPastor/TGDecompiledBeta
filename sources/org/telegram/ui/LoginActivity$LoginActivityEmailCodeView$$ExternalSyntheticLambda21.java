package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivityEmailCodeView f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda21(LoginActivity.LoginActivityEmailCodeView loginActivityEmailCodeView, String str) {
        this.f$0 = loginActivityEmailCodeView;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$14(this.f$1, tLObject, tLRPC$TL_error);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda18 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda18(LoginActivity.PhoneView phoneView) {
        this.f$0 = phoneView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$13(tLObject, tLRPC$TL_error);
    }
}

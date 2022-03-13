package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda32 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC$TL_account_confirmPhone f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda32(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC$TL_account_confirmPhone tLRPC$TL_account_confirmPhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tLRPC$TL_account_confirmPhone;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$21(this.f$1, tLObject, tLRPC$TL_error);
    }
}

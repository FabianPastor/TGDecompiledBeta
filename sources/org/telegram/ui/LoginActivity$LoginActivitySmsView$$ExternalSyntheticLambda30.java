package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda30 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_account_confirmPhone f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda30(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_account_confirmPhone tL_account_confirmPhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_account_confirmPhone;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3864xeb54avar_(this.f$1, tLObject, tL_error);
    }
}

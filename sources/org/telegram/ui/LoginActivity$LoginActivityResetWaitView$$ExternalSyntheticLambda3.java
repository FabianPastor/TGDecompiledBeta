package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivityResetWaitView f$0;

    public /* synthetic */ LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda3(LoginActivity.LoginActivityResetWaitView loginActivityResetWaitView) {
        this.f$0 = loginActivityResetWaitView;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3843xvar_b2e7(tLObject, tL_error);
    }
}

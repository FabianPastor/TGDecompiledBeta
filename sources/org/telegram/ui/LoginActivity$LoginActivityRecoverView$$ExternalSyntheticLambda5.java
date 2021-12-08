package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivityRecoverView f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5(LoginActivity.LoginActivityRecoverView loginActivityRecoverView, String str) {
        this.f$0 = loginActivityRecoverView;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3217x42bba14(this.f$1, tLObject, tL_error);
    }
}

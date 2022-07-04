package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda12 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda12(LoginActivity.PhoneView phoneView, String str) {
        this.f$0 = phoneView;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3912lambda$onNextPressed$18$orgtelegramuiLoginActivity$PhoneView(this.f$1, tLObject, tL_error);
    }
}

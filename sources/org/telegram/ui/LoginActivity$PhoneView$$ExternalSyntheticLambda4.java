package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda4(LoginActivity.PhoneView phoneView) {
        this.f$0 = phoneView;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3264lambda$onNextPressed$13$orgtelegramuiLoginActivity$PhoneView(tLObject, tL_error);
    }
}

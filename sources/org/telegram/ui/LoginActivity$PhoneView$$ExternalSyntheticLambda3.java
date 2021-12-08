package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda3(LoginActivity.PhoneView phoneView) {
        this.f$0 = phoneView;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3254lambda$new$10$orgtelegramuiLoginActivity$PhoneView(tLObject, tL_error);
    }
}

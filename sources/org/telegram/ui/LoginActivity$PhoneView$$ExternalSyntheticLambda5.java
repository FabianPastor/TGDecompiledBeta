package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ TLRPC.TL_auth_sendCode f$2;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda5(LoginActivity.PhoneView phoneView, Bundle bundle, TLRPC.TL_auth_sendCode tL_auth_sendCode) {
        this.f$0 = phoneView;
        this.f$1 = bundle;
        this.f$2 = tL_auth_sendCode;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3266lambda$onNextPressed$15$orgtelegramuiLoginActivity$PhoneView(this.f$1, this.f$2, tLObject, tL_error);
    }
}

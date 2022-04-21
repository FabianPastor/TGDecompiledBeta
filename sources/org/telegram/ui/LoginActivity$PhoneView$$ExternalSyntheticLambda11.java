package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.TL_auth_sendCode f$3;
    public final /* synthetic */ LoginActivity.PhoneInputData f$4;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda11(LoginActivity.PhoneView phoneView, Bundle bundle, String str, TLRPC.TL_auth_sendCode tL_auth_sendCode, LoginActivity.PhoneInputData phoneInputData) {
        this.f$0 = phoneView;
        this.f$1 = bundle;
        this.f$2 = str;
        this.f$3 = tL_auth_sendCode;
        this.f$4 = phoneInputData;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2586lambda$onNextPressed$20$orgtelegramuiLoginActivity$PhoneView(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}

package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLRPC.TL_auth_sendCode f$5;
    public final /* synthetic */ LoginActivity.PhoneInputData f$6;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda5(LoginActivity.PhoneView phoneView, TLRPC.TL_error tL_error, Bundle bundle, TLObject tLObject, String str, TLRPC.TL_auth_sendCode tL_auth_sendCode, LoginActivity.PhoneInputData phoneInputData) {
        this.f$0 = phoneView;
        this.f$1 = tL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
        this.f$4 = str;
        this.f$5 = tL_auth_sendCode;
        this.f$6 = phoneInputData;
    }

    public final void run() {
        this.f$0.m2585lambda$onNextPressed$19$orgtelegramuiLoginActivity$PhoneView(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

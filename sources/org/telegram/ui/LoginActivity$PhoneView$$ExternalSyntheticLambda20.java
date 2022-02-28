package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_auth_sendCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda20 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC$TL_auth_sendCode f$3;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda20(LoginActivity.PhoneView phoneView, Bundle bundle, String str, TLRPC$TL_auth_sendCode tLRPC$TL_auth_sendCode) {
        this.f$0 = phoneView;
        this.f$1 = bundle;
        this.f$2 = str;
        this.f$3 = tLRPC$TL_auth_sendCode;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$21(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}

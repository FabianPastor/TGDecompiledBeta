package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_auth_sendCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC$TL_auth_sendCode f$4;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda8(LoginActivity.PhoneView phoneView, TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject, TLRPC$TL_auth_sendCode tLRPC$TL_auth_sendCode) {
        this.f$0 = phoneView;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
        this.f$4 = tLRPC$TL_auth_sendCode;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$14(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

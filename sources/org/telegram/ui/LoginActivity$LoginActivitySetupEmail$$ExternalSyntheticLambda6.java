package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_sendVerifyEmailCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySetupEmail f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;
    public final /* synthetic */ TLRPC$TL_account_sendVerifyEmailCode f$4;

    public /* synthetic */ LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda6(LoginActivity.LoginActivitySetupEmail loginActivitySetupEmail, TLObject tLObject, Bundle bundle, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_sendVerifyEmailCode tLRPC$TL_account_sendVerifyEmailCode) {
        this.f$0 = loginActivitySetupEmail;
        this.f$1 = tLObject;
        this.f$2 = bundle;
        this.f$3 = tLRPC$TL_error;
        this.f$4 = tLRPC$TL_account_sendVerifyEmailCode;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$7(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityRecoverView f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;

    public /* synthetic */ LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4(LoginActivity.LoginActivityRecoverView loginActivityRecoverView, TLObject tLObject, String str, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = loginActivityRecoverView;
        this.f$1 = tLObject;
        this.f$2 = str;
        this.f$3 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$3(this.f$1, this.f$2, this.f$3);
    }
}

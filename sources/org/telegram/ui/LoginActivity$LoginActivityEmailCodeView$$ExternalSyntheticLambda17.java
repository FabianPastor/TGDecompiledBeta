package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityEmailCodeView f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ LoginActivity$LoginActivityEmailCodeView$$ExternalSyntheticLambda17(LoginActivity.LoginActivityEmailCodeView loginActivityEmailCodeView, TLRPC$TL_error tLRPC$TL_error, String str, TLObject tLObject) {
        this.f$0 = loginActivityEmailCodeView;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = str;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$15(this.f$1, this.f$2, this.f$3);
    }
}

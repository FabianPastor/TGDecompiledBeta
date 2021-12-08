package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityRegisterView f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;

    public /* synthetic */ LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4(LoginActivity.LoginActivityRegisterView loginActivityRegisterView, TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0 = loginActivityRegisterView;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
    }

    public final void run() {
        this.f$0.m3228x8b09128e(this.f$1, this.f$2);
    }
}

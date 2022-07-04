package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityRecoverView f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda8(LoginActivity.LoginActivityRecoverView loginActivityRecoverView, TLObject tLObject, String str, TLRPC.TL_error tL_error) {
        this.f$0 = loginActivityRecoverView;
        this.f$1 = tLObject;
        this.f$2 = str;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.m3814x7ceb2b1(this.f$1, this.f$2, this.f$3);
    }
}

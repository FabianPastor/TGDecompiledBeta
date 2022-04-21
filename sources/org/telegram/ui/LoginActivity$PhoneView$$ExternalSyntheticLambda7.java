package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda7(LoginActivity.PhoneView phoneView, TLRPC.TL_error tL_error, TLObject tLObject, String str) {
        this.f$0 = phoneView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.m2583lambda$onNextPressed$17$orgtelegramuiLoginActivity$PhoneView(this.f$1, this.f$2, this.f$3);
    }
}

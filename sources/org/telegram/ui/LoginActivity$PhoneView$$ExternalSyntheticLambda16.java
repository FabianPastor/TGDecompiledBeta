package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ LoginActivity.PhoneView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda16(LoginActivity.PhoneView phoneView, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = phoneView;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m3261lambda$new$9$orgtelegramuiLoginActivity$PhoneView(this.f$1, this.f$2);
    }
}

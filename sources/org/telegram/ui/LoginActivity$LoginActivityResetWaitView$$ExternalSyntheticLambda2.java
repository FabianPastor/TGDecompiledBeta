package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivityResetWaitView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda2(LoginActivity.LoginActivityResetWaitView loginActivityResetWaitView, TLRPC.TL_error tL_error) {
        this.f$0 = loginActivityResetWaitView;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m2519x63539bc8(this.f$1);
    }
}

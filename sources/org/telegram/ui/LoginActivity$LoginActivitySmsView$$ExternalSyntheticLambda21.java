package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda21 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_account_confirmPhone f$2;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda21(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error, TLRPC.TL_account_confirmPhone tL_account_confirmPhone) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = tL_account_confirmPhone;
    }

    public final void run() {
        this.f$0.m2535xd841113d(this.f$1, this.f$2);
    }
}

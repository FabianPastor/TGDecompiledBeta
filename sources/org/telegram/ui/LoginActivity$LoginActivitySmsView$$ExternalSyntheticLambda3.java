package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3(LoginActivity.LoginActivitySmsView loginActivitySmsView, TLRPC.TL_error tL_error, Bundle bundle, TLObject tLObject) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m3246xcc2a879a(this.f$1, this.f$2, this.f$3);
    }
}

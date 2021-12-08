package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LoginActivity;

public final /* synthetic */ class LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ LoginActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ Bundle f$1;

    public /* synthetic */ LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(LoginActivity.LoginActivitySmsView loginActivitySmsView, Bundle bundle) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = bundle;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3247xfa0321f9(this.f$1, tLObject, tL_error);
    }
}

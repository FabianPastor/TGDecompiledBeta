package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ ChangePhoneActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ TLRPC.TL_auth_resendCode f$2;

    public /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda9(ChangePhoneActivity.LoginActivitySmsView loginActivitySmsView, Bundle bundle, TLRPC.TL_auth_resendCode tL_auth_resendCode) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = bundle;
        this.f$2 = tL_auth_resendCode;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1584x7413fc3(this.f$1, this.f$2, tLObject, tL_error);
    }
}

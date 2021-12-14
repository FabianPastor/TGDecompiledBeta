package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_auth_resendCode;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChangePhoneActivity;

public final /* synthetic */ class ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ChangePhoneActivity.LoginActivitySmsView f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC$TL_auth_resendCode f$4;

    public /* synthetic */ ChangePhoneActivity$LoginActivitySmsView$$ExternalSyntheticLambda6(ChangePhoneActivity.LoginActivitySmsView loginActivitySmsView, TLRPC$TL_error tLRPC$TL_error, Bundle bundle, TLObject tLObject, TLRPC$TL_auth_resendCode tLRPC$TL_auth_resendCode) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
        this.f$4 = tLRPC$TL_auth_resendCode;
    }

    public final void run() {
        this.f$0.lambda$resendCode$2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

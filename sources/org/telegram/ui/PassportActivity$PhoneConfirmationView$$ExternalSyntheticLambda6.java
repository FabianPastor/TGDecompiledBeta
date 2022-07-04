package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ PassportActivity.PhoneConfirmationView f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ Bundle f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC.TL_auth_resendCode f$4;

    public /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda6(PassportActivity.PhoneConfirmationView phoneConfirmationView, TLRPC.TL_error tL_error, Bundle bundle, TLObject tLObject, TLRPC.TL_auth_resendCode tL_auth_resendCode) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = tL_error;
        this.f$2 = bundle;
        this.f$3 = tLObject;
        this.f$4 = tL_auth_resendCode;
    }

    public final void run() {
        this.f$0.m4126x4c7bbaac(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

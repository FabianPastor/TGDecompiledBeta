package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.PhoneConfirmationView f$0;
    public final /* synthetic */ Bundle f$1;
    public final /* synthetic */ TLRPC.TL_auth_resendCode f$2;

    public /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda9(PassportActivity.PhoneConfirmationView phoneConfirmationView, Bundle bundle, TLRPC.TL_auth_resendCode tL_auth_resendCode) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = bundle;
        this.f$2 = tL_auth_resendCode;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2801x4db20d8b(this.f$1, this.f$2, tLObject, tL_error);
    }
}

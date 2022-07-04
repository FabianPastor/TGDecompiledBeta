package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.PhoneConfirmationView f$0;
    public final /* synthetic */ TLRPC.TL_account_verifyPhone f$1;

    public /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda10(PassportActivity.PhoneConfirmationView phoneConfirmationView, TLRPC.TL_account_verifyPhone tL_account_verifyPhone) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = tL_account_verifyPhone;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4124xdbvar_e59(this.f$1, tLObject, tL_error);
    }
}

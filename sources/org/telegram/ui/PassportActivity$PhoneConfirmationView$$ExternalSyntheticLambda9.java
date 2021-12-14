package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ PassportActivity.PhoneConfirmationView f$0;
    public final /* synthetic */ TLRPC$TL_account_verifyPhone f$1;

    public /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda9(PassportActivity.PhoneConfirmationView phoneConfirmationView, TLRPC$TL_account_verifyPhone tLRPC$TL_account_verifyPhone) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = tLRPC$TL_account_verifyPhone;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onNextPressed$7(this.f$1, tLObject, tLRPC$TL_error);
    }
}

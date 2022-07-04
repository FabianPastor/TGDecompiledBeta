package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ PassportActivity.PhoneConfirmationView f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_account_verifyPhone f$2;

    public /* synthetic */ PassportActivity$PhoneConfirmationView$$ExternalSyntheticLambda6(PassportActivity.PhoneConfirmationView phoneConfirmationView, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_verifyPhone tLRPC$TL_account_verifyPhone) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_account_verifyPhone;
    }

    public final void run() {
        this.f$0.lambda$onNextPressed$6(this.f$1, this.f$2);
    }
}

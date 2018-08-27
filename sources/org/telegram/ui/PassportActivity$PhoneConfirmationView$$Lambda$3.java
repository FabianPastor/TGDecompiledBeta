package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$3 implements RequestDelegate {
    private final PhoneConfirmationView arg$1;
    private final TL_account_verifyPhone arg$2;

    PassportActivity$PhoneConfirmationView$$Lambda$3(PhoneConfirmationView phoneConfirmationView, TL_account_verifyPhone tL_account_verifyPhone) {
        this.arg$1 = phoneConfirmationView;
        this.arg$2 = tL_account_verifyPhone;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onNextPressed$6$PassportActivity$PhoneConfirmationView(this.arg$2, tLObject, tL_error);
    }
}

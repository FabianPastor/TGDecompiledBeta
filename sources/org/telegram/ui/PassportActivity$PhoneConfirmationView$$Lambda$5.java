package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$5 implements Runnable {
    private final PhoneConfirmationView arg$1;
    private final TL_error arg$2;
    private final TL_account_verifyPhone arg$3;

    PassportActivity$PhoneConfirmationView$$Lambda$5(PhoneConfirmationView phoneConfirmationView, TL_error tL_error, TL_account_verifyPhone tL_account_verifyPhone) {
        this.arg$1 = phoneConfirmationView;
        this.arg$2 = tL_error;
        this.arg$3 = tL_account_verifyPhone;
    }

    public void run() {
        this.arg$1.lambda$null$5$PassportActivity$PhoneConfirmationView(this.arg$2, this.arg$3);
    }
}

package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.PhoneConfirmationView;
import org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.PassportActivity$PhoneConfirmationView$$Lambda$7 */
final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$7 implements Runnable {
    private final PhoneConfirmationView arg$1;
    private final TL_error arg$2;
    private final TL_account_verifyPhone arg$3;

    PassportActivity$PhoneConfirmationView$$Lambda$7(PhoneConfirmationView phoneConfirmationView, TL_error tL_error, TL_account_verifyPhone tL_account_verifyPhone) {
        this.arg$1 = phoneConfirmationView;
        this.arg$2 = tL_error;
        this.arg$3 = tL_account_verifyPhone;
    }

    public void run() {
        this.arg$1.lambda$null$6$PassportActivity$PhoneConfirmationView(this.arg$2, this.arg$3);
    }
}

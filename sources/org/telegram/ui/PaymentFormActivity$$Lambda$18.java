package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;

final /* synthetic */ class PaymentFormActivity$$Lambda$18 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final boolean arg$2;
    private final String arg$3;
    private final String arg$4;
    private final TL_account_updatePasswordSettings arg$5;

    PaymentFormActivity$$Lambda$18(PaymentFormActivity paymentFormActivity, boolean z, String str, String str2, TL_account_updatePasswordSettings tL_account_updatePasswordSettings) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = z;
        this.arg$3 = str;
        this.arg$4 = str2;
        this.arg$5 = tL_account_updatePasswordSettings;
    }

    public void run() {
        this.arg$1.lambda$sendSavePassword$28$PaymentFormActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

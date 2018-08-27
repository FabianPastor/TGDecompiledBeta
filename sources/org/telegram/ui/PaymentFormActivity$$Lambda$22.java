package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PaymentFormActivity$$Lambda$22 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final String arg$4;
    private final TL_account_getPassword arg$5;

    PaymentFormActivity$$Lambda$22(PaymentFormActivity paymentFormActivity, TL_error tL_error, TLObject tLObject, String str, TL_account_getPassword tL_account_getPassword) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = str;
        this.arg$5 = tL_account_getPassword;
    }

    public void run() {
        this.arg$1.lambda$null$39$PaymentFormActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

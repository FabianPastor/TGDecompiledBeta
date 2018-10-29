package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PaymentFormActivity$$Lambda$25 implements Runnable {
    private final PaymentFormActivity arg$1;
    private final TLObject arg$2;
    private final TL_error arg$3;
    private final TL_account_getTmpPassword arg$4;

    PaymentFormActivity$$Lambda$25(PaymentFormActivity paymentFormActivity, TLObject tLObject, TL_error tL_error, TL_account_getTmpPassword tL_account_getTmpPassword) {
        this.arg$1 = paymentFormActivity;
        this.arg$2 = tLObject;
        this.arg$3 = tL_error;
        this.arg$4 = tL_account_getTmpPassword;
    }

    public void run() {
        this.arg$1.lambda$null$36$PaymentFormActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
